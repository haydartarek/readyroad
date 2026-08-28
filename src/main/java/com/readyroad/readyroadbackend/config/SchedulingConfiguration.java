package com.readyroad.readyroadbackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Enables scheduled jobs in normal application modes.
 * Scheduling is disabled when localhost is connected to the
 * production database through the production-mirror profile.
 */
@Configuration
@EnableScheduling
@Profile("!production-mirror")
public class SchedulingConfiguration {
}