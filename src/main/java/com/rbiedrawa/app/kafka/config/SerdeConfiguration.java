package com.rbiedrawa.app.kafka.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.rbiedrawa.app.proto.accounts.Account;

import io.confluent.kafka.streams.serdes.protobuf.KafkaProtobufSerde;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@AllArgsConstructor
public class SerdeConfiguration {

	private final KafkaProperties kafkaProperties;

	@Bean
	public KafkaProtobufSerde<Account> accountSerde() {
		var serde = new KafkaProtobufSerde<>(Account.class);

		serde.configure(kafkaProperties.getProperties(), false);
		log.debug("Created serde for {}", Account.class);
		return serde;
	}
}
