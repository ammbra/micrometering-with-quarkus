package org.acme.config;

import io.quarkus.arc.config.ConfigProperties;
import io.quarkus.runtime.configuration.ProfileManager;

import javax.resource.spi.ConfigProperty;

@ConfigProperties(prefix = "global")
public class GlobalTagsConfig {

    public static final String PROFILE = "profile";
    public static final String REGION = "region";

    public String region;
    public String customMeter;
}
