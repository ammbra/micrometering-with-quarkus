package org.acme.metric.timer;

import io.micrometer.core.instrument.Meter;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class DynamicMultiTaggedTimerTest {

    private PrometheusMeterRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
    }

    @Test
    public void increment() {
        DynamicMultiTaggedTimer multiTaggedTimer = new DynamicMultiTaggedTimer("timerId", registry, "language", "content");
        multiTaggedTimer.decorate("en", "hello").record(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });


        multiTaggedTimer.decorate("no", "hello").record(30, TimeUnit.MINUTES);
        multiTaggedTimer.decorate("fr", "salut").record(30, TimeUnit.MINUTES);
        multiTaggedTimer.decorate("ro", "salut").record(2, TimeUnit.HOURS);
        multiTaggedTimer.decorate("en", "hi").record(10, TimeUnit.MINUTES);
        multiTaggedTimer.decorate("fr", "bonjour").record(15, TimeUnit.MINUTES);
        List<Meter> meters = registry.getMeters();
        assertEquals(6, meters.size());
    }


    @Test
    public void incrementWithWrongArguments() {
        DynamicMultiTaggedTimer multiTaggedTimer = new DynamicMultiTaggedTimer("timerId", registry,  "language", "content");
        assertThrows(IllegalArgumentException.class, () -> {
            multiTaggedTimer.decorate("en").record(800, TimeUnit.MINUTES);
        });
    }
    @AfterEach
    void tearDown() {
        registry = null;
    }
}