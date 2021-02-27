package org.acme.metric.counter;

import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DynamicMultiTaggedCounterTest {

    private PrometheusMeterRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
    }

    @Test
    public void increment() {
        DynamicMultiTaggedCounter dynamicMultiTaggedCounter = new DynamicMultiTaggedCounter("people", registry, "sport", "group");
        dynamicMultiTaggedCounter.increment("football", "young");
        dynamicMultiTaggedCounter.increment("football", "young");
        dynamicMultiTaggedCounter.increment("baseball", "young");
        dynamicMultiTaggedCounter.increment("football", "old");
        dynamicMultiTaggedCounter.increment("football", "child");
        dynamicMultiTaggedCounter.increment("football", "old");
        dynamicMultiTaggedCounter.increment("baseball", "child");
        dynamicMultiTaggedCounter.increment("basketball", "adult");

        assertEquals(6, registry.getMeters().size());
    }

    @Test
    public void incrementWithWrongArguments() {
        DynamicMultiTaggedCounter dynamicMultiTaggedCounter = new DynamicMultiTaggedCounter("sheep", registry, "color", "age-group");
        assertThrows(IllegalArgumentException.class, () -> {
            dynamicMultiTaggedCounter.increment("football");
        });
    }

    @AfterEach
    void tearDown() {
        registry = null;
    }
}