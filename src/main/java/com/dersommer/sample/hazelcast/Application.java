package com.dersommer.sample.hazelcast;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@SpringBootApplication
@EnableAutoConfiguration
@Component
@ComponentScan(basePackages = { "com.dersommer.sample" })
public class Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    @Autowired
    private Environment env;

    @PostConstruct
    public void initApplication() throws IOException {
        if (env.getActiveProfiles().length == 0) {
            LOGGER.warn("STARTUP: No Spring profile configured, running with default configuration");
        } else {
            LOGGER.info("STARTUP: Running with Spring profile(s) : {}", Arrays.toString(env.getActiveProfiles()));
            Collection<String> activeProfiles = Arrays.asList(env.getActiveProfiles());
            if (activeProfiles.size() > 1) {
                LOGGER.error("STARTUP: You have misconfigured your application! It should not run with multiple profiles at the same time.");
            }
        }
    }

    public static void main(String[] args) {
        LOGGER.info("STARTUP starting...");
        new SpringApplicationBuilder().sources(Application.class)
                                      .run(args);
        LOGGER.info("STARTUP completed...");
    }
}
