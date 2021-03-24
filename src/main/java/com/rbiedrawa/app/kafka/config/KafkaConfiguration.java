package com.rbiedrawa.app.kafka.config;

import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

import org.apache.kafka.clients.admin.NewTopic;

@EnableKafka
@Configuration
public class KafkaConfiguration {

	public static final String TOPIC_ACCOUNT_EVENTS = "account.events";
	public static final String ACCOUNT_STORE = "accounts.store";

	@Bean
	NewTopic ordersTopic() {
		return TopicBuilder.name(TOPIC_ACCOUNT_EVENTS)
						   .partitions(6)
						   .configs(Map.of("confluent.value.subject.name.strategy", "io.confluent.kafka.serializers.subject.RecordNameStrategy"))
						   .build();
	}
}
