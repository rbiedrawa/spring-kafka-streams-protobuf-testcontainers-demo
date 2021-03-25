package com.rbiedrawa.app.kafka;

import java.util.Map;

import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;

import org.apache.kafka.streams.StreamsConfig;

public final class StreamsFactory {

	private StreamsFactory() {
	}

	public static StreamsBuilderFactoryBean newKStreamConfigurationFrom(String applicationId, KafkaProperties defaultProperties) throws Exception {
		Map<String, Object> streamsProperties = defaultProperties.buildStreamsProperties();
		streamsProperties.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationId);

		var streamsConfig = new KafkaStreamsConfiguration(streamsProperties);
		StreamsBuilderFactoryBean streamsBuilderFactoryBean = new StreamsBuilderFactoryBean(streamsConfig);
		streamsBuilderFactoryBean.afterPropertiesSet();
		return streamsBuilderFactoryBean;
	}
}
