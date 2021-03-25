package com.rbiedrawa.app.kafka.utils;

import static org.apache.kafka.streams.StreamsConfig.BOOTSTRAP_SERVERS_CONFIG;

import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.function.Consumer;

import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;

// TODO: Refactor to TopologyTest abstract
public abstract class TopologyTestDriverFactory {

	private static final Properties DEFAULT_TOPOLOGY_CONFIG = defaultTestTopologyConfig();

	public static TopologyTestDriver create(Consumer<StreamsBuilder> streamsBuilderConsumer) {
		return create(streamsBuilderConsumer, DEFAULT_TOPOLOGY_CONFIG);
	}

	public static TopologyTestDriver create(Consumer<StreamsBuilder> streamsBuilderConsumer, Properties config) {
		var streamsBuilder = new StreamsBuilder();
		streamsBuilderConsumer.accept(streamsBuilder);

		Topology topology = streamsBuilder.build();
		System.out.println("---------- --------- ---------- ------");
		System.out.println(topology.describe());

		return new TopologyTestDriver(topology, config);
	}

	private static Properties defaultTestTopologyConfig() {
		Properties config = new Properties();
		config.putAll(Map.of(StreamsConfig.APPLICATION_ID_CONFIG, "topology-test-" + UUID.randomUUID().toString(),
							 BOOTSTRAP_SERVERS_CONFIG, "ignored:9092"));
		return config;
	}

}
