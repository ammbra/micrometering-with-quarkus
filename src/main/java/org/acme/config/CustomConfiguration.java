package org.acme.config;

import io.micrometer.core.instrument.ImmutableTag;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.config.MeterFilter;
import io.quarkus.runtime.configuration.ProfileManager;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;

@Singleton
public class CustomConfiguration {

    @Inject
    GlobalTagsConfig tagsConfig;

    @Produces
    @Singleton
    public MeterFilter configureAllRegistries() {
        return MeterFilter.commonTags(Arrays.asList(
                Tag.of(GlobalTagsConfig.PROFILE, ProfileManager.getActiveProfile()),
                Tag.of(GlobalTagsConfig.REGION, tagsConfig.region())));
    }

    @Produces
    @Singleton
    public MeterFilter configureMeterFromEnvironment() {
        return new MeterFilter() {
            @Override
            public Meter.Id map(Meter.Id id) {
                String prefix = tagsConfig.customMeter();
                if(id.getName().startsWith(prefix)) {
                    return id.withName(prefix +"." + id.getName())
                            .withTag(new ImmutableTag(prefix+".tag", "value"));
                }
                return id;
            }
        };
    }
}

