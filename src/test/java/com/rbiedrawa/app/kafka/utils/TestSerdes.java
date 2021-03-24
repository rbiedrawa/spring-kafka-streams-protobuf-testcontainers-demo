package com.rbiedrawa.app.kafka.utils;

import java.util.Map;

import com.google.protobuf.Message;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.streams.serdes.protobuf.KafkaProtobufSerde;

public abstract class TestSerdes {

	private TestSerdes() { }

	public static <T extends Message>KafkaProtobufSerde<T> from(Class<T> clazz) {
		var serde = new KafkaProtobufSerde<>(clazz);
		serde.configure(Map.of(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "mock://test-schema-registry"),
						false);
		return serde;
	}

}
