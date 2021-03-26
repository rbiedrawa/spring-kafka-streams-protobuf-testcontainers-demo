package com.rbiedrawa.app.containers;

import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.utility.DockerImageName;

public abstract class AbstractContainerBaseIT {
	private static final KafkaContainer KAFKA_CONTAINER;

	private static final SchemaRegistryContainer SCHEMA_REGISTRY_CONTAINER;

	static {
		Network sharedNetwork = Network.newNetwork();

		KAFKA_CONTAINER = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.1.1"))
			.withNetwork(sharedNetwork);

		SCHEMA_REGISTRY_CONTAINER = new SchemaRegistryContainer(DockerImageName.parse("confluentinc/cp-schema-registry:6.1.1"))
			.withKafka(KAFKA_CONTAINER);

		KAFKA_CONTAINER.start();
		SCHEMA_REGISTRY_CONTAINER.start();
	}

	protected static String getBootstrapServers() {
		return KAFKA_CONTAINER.getBootstrapServers();
	}

	 protected  static String getSchemaRegistryUrl() {
		return SCHEMA_REGISTRY_CONTAINER.getSchemaRegistryUrl();
	}

}
