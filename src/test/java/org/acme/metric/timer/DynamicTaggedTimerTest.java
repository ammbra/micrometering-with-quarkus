package org.acme.metric.timer;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class DynamicTaggedTimerTest {

    private PrometheusMeterRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
    }

    @Test
    public void decorate() {
        DynamicTaggedTimer taggedTimer = new DynamicTaggedTimer("timerId", "greeting", registry);
        taggedTimer.decorate("hello").record(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        taggedTimer.decorate("bonjour").record(10, TimeUnit.MINUTES);
        taggedTimer.decorate("bonsoir").record(2, TimeUnit.HOURS);
        taggedTimer.decorate("hello").record(1, TimeUnit.MINUTES);
        taggedTimer.decorate("hello").record(15, TimeUnit.MINUTES);
        List<Meter> meters = registry.getMeters();
        assertEquals(3, meters.size());
    }


    @AfterEach
    void tearDown() {
        registry = null;
    }
}