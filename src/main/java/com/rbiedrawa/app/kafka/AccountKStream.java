package com.rbiedrawa.app.kafka;


import static com.rbiedrawa.app.kafka.config.KafkaConfiguration.ACCOUNT_STORE;
import static com.rbiedrawa.app.kafka.config.KafkaConfiguration.TOPIC_ACCOUNT_EVENTS;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;

import com.rbiedrawa.app.proto.accounts.Account;

import io.confluent.kafka.streams.serdes.protobuf.KafkaProtobufSerde;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AccountKStream {
	public static final String STREAMS_BUILDER_BEAN_NAME = "accountAggregateStreamsFactory";

	private final KafkaProtobufSerde<Account> accountSerde;

	@Bean(name = STREAMS_BUILDER_BEAN_NAME)
	StreamsBuilderFactoryBean streamsBuilderFactoryBean(KafkaProperties defaultProperties) throws Exception {
		String applicationId = "accounts-aggregator";
		return StreamsFactory.from(defaultProperties, applicationId);
	}

	@Bean
	KTable<String, Account> accounts(@Qualifier(AccountKStream.STREAMS_BUILDER_BEAN_NAME) StreamsBuilder builder) {
		return builder.table(TOPIC_ACCOUNT_EVENTS, Consumed.with(Serdes.String(), accountSerde), Materialized.as(ACCOUNT_STORE));
	}

}