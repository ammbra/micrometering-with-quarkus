package org.acme.metric.counter;

import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DynamicTaggedCounterTest {

    @Test
    public void increment() {
        PrometheusMeterRegistry registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
        DynamicTaggedCounter dynamicTaggedCounter = new DynamicTaggedCounter("greeting", "language", registry);
        dynamicTaggedCounter.increment("en");
        dynamicTaggedCounter.increment("fr");
        dynamicTaggedCounter.increment("fr");
        dynamicTaggedCounter.increment("en");
        dynamicTaggedCounter.increment("ro");
        dynamicTaggedCounter.increment("en");
        dynamicTaggedCounter.increment("en");
        dynamicTaggedCounter.increment("en");
        assertEquals(3, registry.getMeters().size());
    }

}