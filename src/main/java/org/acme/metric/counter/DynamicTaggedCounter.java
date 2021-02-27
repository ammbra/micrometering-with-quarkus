package org.acme.metric.counter;

import io.micrometer.core.instrument.*;
import org.acme.metric.CommonMetricDetails;


public class DynamicTaggedCounter extends CommonMetricDetails {

    private final String tagName;

    public DynamicTaggedCounter(String identifier, String tagName, MeterRegistry registry) {
        super(identifier, registry);
        this.tagName = tagName;
    }

    public void increment(String tagValue) {
        Counter counter = registry.counter(identifier, tagName, tagValue);
        counter.getId().withTag(new ImmutableTag(tagName, tagValue));
        counter.increment();
    }


}
