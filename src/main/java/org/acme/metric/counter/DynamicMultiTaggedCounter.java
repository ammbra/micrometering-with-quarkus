package org.acme.metric.counter;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.ImmutableTag;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import org.acme.metric.CommonMetricDetails;

import java.util.*;

public class DynamicMultiTaggedCounter extends CommonMetricDetails {
    private final List<String> tagNames;

    public DynamicMultiTaggedCounter(String identifier, MeterRegistry registry, String ... tags) {
        super(identifier, registry);
        this.tagNames = Arrays.asList(tags.clone());
    }

    public void increment(String... tagValues){
        List<String> adaptedValues = Arrays.asList(tagValues);
        int size = this.tagNames.size();
        if(adaptedValues.size() != size) {
            throw new IllegalArgumentException("Counter tag values mismatch the tag names! Expected args are "+ this.tagNames.toString()+", provided tags were "+adaptedValues);
        }

        List<Tag> tags = new ArrayList<>(size);
        for(int i = 0; i< size; i++) {
            tags.add(new ImmutableTag(this.tagNames.get(i), tagValues[i]));
        }
        Counter counter = registry.counter(identifier, tags);
        counter.getId().withTags(tags);
        counter.increment();
    }

}
