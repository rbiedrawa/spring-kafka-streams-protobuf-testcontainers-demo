package com.rbiedrawa.app.kafka.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

import org.apache.kafka.clients.admin.NewTopic;

@EnableKafka
@Configuration
public class KafkaConfiguration {

	@Bean
	NewTopic ordersTopic() {
		return TopicBuilder.name(KafkaTopics.ACCOUNTS)
						   .partitions(KafkaTopics.DEFAULT_PARTITION_COUNT)
						   .build();
	}
}
