package org.acme.metric;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class CommonMetricDetails {
    protected String identifier;
    protected MeterRegistry registry;
}
