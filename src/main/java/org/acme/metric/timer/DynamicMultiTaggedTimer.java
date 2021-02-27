package org.acme.metric.timer;

import io.micrometer.core.instrument.*;
import io.micrometer.core.instrument.Timer;
import org.acme.metric.CommonMetricDetails;

import java.util.*;


public class DynamicMultiTaggedTimer extends CommonMetricDetails {

    protected List<String> tagNames;

    public DynamicMultiTaggedTimer(String name, MeterRegistry registry, String... tags) {
        super(name, registry);
        this.tagNames = Arrays.asList(tags.clone());
    }

    public Timer decorate(String ... tagValues) {
        List<String> adaptedValues = Arrays.asList(tagValues);
        if(adaptedValues.size() != tagNames.size()) {
            throw new IllegalArgumentException("Timer tag values mismatch the tag names! Expected args are " + tagNames.toString() + ", provided tags are " + adaptedValues);
        }
        int size = tagNames.size();
        List<Tag> tags = new ArrayList<>(size);
        for(int i = 0; i<size; i++) {
            tags.add(new ImmutableTag(tagNames.get(i), tagValues[i]));
        }

        Timer timer = registry.timer(identifier, tags);
        timer.getId().withTags(tags);
        return timer;
    }

}
