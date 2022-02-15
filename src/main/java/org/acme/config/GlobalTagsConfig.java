package org.acme.config;

import io.quarkus.runtime.annotations.StaticInitSafe;
import io.smallrye.config.ConfigMapping;

@StaticInitSafe
@ConfigMapping(prefix = "global")
interface GlobalTagsConfig {

   String PROFILE = "profile";
   String REGION = "region";

   String region();
   String customMeter();
}