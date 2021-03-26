package com.rbiedrawa.app.faker.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;

@Profile("dummy_data_gen")
@Configuration
@EnableScheduling
public class ScheduleConfiguration {
}
