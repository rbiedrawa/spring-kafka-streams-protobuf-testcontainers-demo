package com.rbiedrawa.app;

import static org.assertj.core.api.BDDAssertions.then;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import org.junit.jupiter.api.Test;


class ApplicationIT extends AbstractBaseIT {

	@Autowired
	private Environment environment;

	@Test
	void contextLoads() {
	}

	@Test
	void should_set_kafka_bootstrap_servers() {
		// Given / When
		String bootstrapServers = environment.getProperty("spring.kafka.bootstrap-servers");

		// Then
		then(bootstrapServers).isEqualTo(getBootstrapServers());
	}

	@Test
	void should_set_schema_registry_url() {
		// Given / When
		String schemaRegistryUrl = environment.getProperty("spring.kafka.properties.schema.registry.url");

		// Then
		then(schemaRegistryUrl).isEqualTo(getSchemaRegistryUrl());
	}

}
