package org.acme.endpoint;

import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Timer;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.acme.dto.MessageDTO;
import org.acme.metric.counter.DynamicMultiTaggedCounter;
import org.acme.metric.counter.DynamicTaggedCounter;
import org.acme.metric.timer.DynamicMultiTaggedTimer;
import org.acme.metric.timer.DynamicTaggedTimer;
import org.acme.model.Message;
import org.acme.service.CustomMessageService;
import org.eclipse.microprofile.metrics.annotation.ConcurrentGauge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

@Path("/api/v1")
public class ExampleEndpoint {
    public static final String URI = "uri";
    public static final String API_GREET = "api.greet";
    public static final String EMPTY = "empty";
    public static final String CUSTOM_API_GREET = "custom.api.greet";
    public static final String LANGUAGE = "language";
    private static final Logger LOGGER = LoggerFactory.getLogger(ExampleEndpoint.class);
    @Inject
    protected PrometheusMeterRegistry registry;

    @Inject
    protected CustomMessageService messageService;

    private DynamicTaggedCounter dynamicTaggedCounter;
    private DynamicMultiTaggedCounter dynamicMultiTaggedCounter;
    private DynamicTaggedTimer dynamicTaggedTimer;
    private DynamicMultiTaggedTimer dynamicMultiTaggedTimer;

    @PostConstruct
    protected void init() {
        this.dynamicTaggedCounter = new DynamicTaggedCounter("another.requests.count", CUSTOM_API_GREET, registry);
        this.dynamicTaggedTimer = new DynamicTaggedTimer("another.requests.duration", CUSTOM_API_GREET, registry);
        this.dynamicMultiTaggedTimer = new DynamicMultiTaggedTimer("other.requests.duration", registry, LANGUAGE, CUSTOM_API_GREET);
        this.dynamicMultiTaggedCounter = new DynamicMultiTaggedCounter("other.requests", registry, LANGUAGE, CUSTOM_API_GREET);
    }

    @GET
    @Path("find")
    @Produces(MediaType.TEXT_PLAIN)
    @Timed(value = "greetings.all", longTask = true, extraTags = {URI, API_GREET})
    @Counted(value = "http.get.requests", extraTags = {URI, API_GREET})
    public List<Message> findAll() {
        return messageService.findAll();
    }

    @GET
    @Path("detect/{languageTag}")
    @Produces(MediaType.TEXT_PLAIN)
//    @Timed(value = "greetings.lang", longTask = true, extraTags = {URI, API_GREET})
//    @Counted(value = "http.get.lang.requests", extraTags = {URI, API_GREET})
    @ConcurrentGauge(name = "http.get.lang.requests", tags = {URI, API_GREET})
    public List<Message> filterGreetings(@PathParam("languageTag") String languageTag) {
        AtomicReference<List<Message>> messages = new AtomicReference<>();
        var locale = Locale.forLanguageTag(languageTag);
        dynamicTaggedTimer.decorate(languageTag).record(() -> messages.set(messageService.filterMessages(locale)));
        return messages.get();
    }

    @PUT
    @Path("make/{languageTag}/{content}")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    @Timed(value = "greeting.generator", extraTags = {URI, API_GREET})
    @Counted(value = "http.put.requests", extraTags = {URI, API_GREET})
    public String generateGreeting(@PathParam("languageTag") String languageTag, @PathParam("content") String content) {
        Locale locale = Locale.forLanguageTag(languageTag);
        MessageDTO dto = new MessageDTO(locale, content);
        if ( Math.random() > 0.8 ) {
            var exceptionalTag = "exceptional" + content;
            dynamicMultiTaggedTimer.decorate(languageTag, exceptionalTag).record(() -> {
                try {
                    Thread.sleep(1 + (long) (Math.random() * 500));
                } catch (InterruptedException e) {
                    LOGGER.error("Error occured during long running operation ", e);
                }
            });
            dynamicMultiTaggedCounter.increment(languageTag, exceptionalTag);
        } else {
            dynamicMultiTaggedCounter.increment(languageTag, content);
        }

        messageService.createMessage(dto);
        return content;
    }

    @GET
    @Path("find/{content}")
    @Produces(MediaType.TEXT_PLAIN)
    @Timed(value = "greetings.specific", longTask = true, extraTags = {URI, API_GREET})
    @Counted(value = "http.get.specific.requests", extraTags = {URI, API_GREET})
    public List<Message> findGreetings(@PathParam("content") String content) {
        AtomicReference<List<Message>> messages = new AtomicReference<>();
        if ( !content.isEmpty() ) {
            dynamicTaggedTimer.decorate(content).record(() -> {
                List<Message> greetings = messageService.findMessages(content);
                messages.set(greetings);
            });
        }
        if ( messages.get().size() > 0 ) {
            dynamicTaggedCounter.increment(content);
        } else {
            dynamicTaggedCounter.increment(EMPTY);
        }
        return messages.get();
    }

    @GET
    @Path("search")
    @Produces(MediaType.TEXT_PLAIN)
    public List<Message> search(@QueryParam("content") String content) {
        Timer.Sample sample = Timer.start(registry);
        List<String> input = Arrays.asList(content.split(","));
        DistributionSummary.builder("onsearch.request").scale(100).serviceLevelObjectives(50, 75, 90, 95).publishPercentiles()
                .publishPercentileHistogram().tag("content", input.toString()).register(registry).record(input.size());
        List<Message> messages = messageService.search(input);
        sample.stop(registry.timer("onsearch.request.timer", "content", input.toString()));
        return messages;
    }


}