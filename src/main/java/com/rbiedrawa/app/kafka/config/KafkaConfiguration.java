package com.rbiedrawa.app.kafka.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

import org.apache.kafka.clients.admin.NewTopic;

@EnableKafka
@Configuration
public class KafkaConfiguration {

	public static final String TOPIC_ACCOUNT_EVENTS = "account.events";
	public static final int DEFAULT_PARTITION_COUNT = 6;

	@Bean
	NewTopic ordersTopic() {
		return TopicBuilder.name(TOPIC_ACCOUNT_EVENTS)
						   .partitions(DEFAULT_PARTITION_COUNT)
						   .build();
	}
}
