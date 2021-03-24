package com.rbiedrawa.app.kafka;


import static com.rbiedrawa.app.kafka.config.KafkaConfiguration.ACCOUNT_STORE;
import static com.rbiedrawa.app.kafka.config.KafkaConfiguration.TOPIC_ACCOUNT_EVENTS;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;

import com.rbiedrawa.app.proto.accounts.Account;

import io.confluent.kafka.streams.serdes.protobuf.KafkaProtobufSerde;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;

@Configuration
@EnableKafkaStreams
@RequiredArgsConstructor
public class AccountKStream {

	private final KafkaProtobufSerde<Account> accountSerde;

	@Bean
	public KTable<String, Account> accounts(StreamsBuilder builder) {
		return builder.table(TOPIC_ACCOUNT_EVENTS, Consumed.with(Serdes.String(), accountSerde), Materialized.as(ACCOUNT_STORE));
	}

}
