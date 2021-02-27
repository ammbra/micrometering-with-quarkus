package org.acme.metric.timer;

import io.micrometer.core.instrument.ImmutableTag;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.acme.metric.CommonMetricDetails;


public class DynamicTaggedTimer extends CommonMetricDetails {

    private final String tagName;

    public DynamicTaggedTimer(String identifier, String tagName, MeterRegistry registry) {
        super(identifier, registry);
        this.tagName = tagName;
    }

    public Timer decorate(String tagValue){
        Timer timer = registry.timer(identifier, tagName, tagValue);
        timer.getId().withTag(new ImmutableTag(tagName, tagValue));
        return timer;
    }

}
