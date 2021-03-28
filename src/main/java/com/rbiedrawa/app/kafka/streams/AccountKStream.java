package com.rbiedrawa.app.kafka.streams;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;

import com.rbiedrawa.app.kafka.config.KafkaTopics;
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
	public static final String ACCOUNT_STORE = "accounts.store";

	private final KafkaProtobufSerde<Account> accountSerde;

	@Bean(name = STREAMS_BUILDER_BEAN_NAME)
	StreamsBuilderFactoryBean streamsBuilderFactoryBean(KafkaProperties defaultProperties) throws Exception {
		return StreamsFactory.newKStreamConfigurationFrom("account-aggregator", defaultProperties);
	}

	@Bean
	KTable<String, Account> accountAggregatorStream(@Qualifier(AccountKStream.STREAMS_BUILDER_BEAN_NAME) StreamsBuilder builder) {
		return builder.table(KafkaTopics.ACCOUNTS, Consumed.with(Serdes.String(), accountSerde), Materialized.as(ACCOUNT_STORE));
	}

}
