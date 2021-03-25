package com.rbiedrawa.app.kafka.streams;


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
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.Stores;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AccountsPerCountryKStream {
	public static final String STREAMS_BUILDER_BEAN_NAME = "accountsPerCountryStreamsFactory";
	public static final String ACCOUNTS_PER_COUNTRY_STORE = "accounts_per_country.store";

	private final KafkaProtobufSerde<Account> accountSerde;

	@Bean(name = STREAMS_BUILDER_BEAN_NAME)
	StreamsBuilderFactoryBean streamsBuilderFactoryBean(KafkaProperties defaultProperties) throws Exception {
		return StreamsFactory.newKStreamConfigurationFrom("accounts-per-country", defaultProperties);
	}

	@Bean
	KTable<String, Long> accountsPerCountryStream(@Qualifier(
		AccountsPerCountryKStream.STREAMS_BUILDER_BEAN_NAME) StreamsBuilder builder) {

		return builder.stream(TOPIC_ACCOUNT_EVENTS, Consumed.with(Serdes.String(), accountSerde))
					  .map((accountId, account) -> KeyValue.pair(account.getCountryCode().toLowerCase(), account.getCountryCode().toLowerCase()))
					  .groupByKey(Grouped.with(Serdes.String(), Serdes.String()))
					  .count(Materialized.<String, Long>as(Stores.persistentKeyValueStore(ACCOUNTS_PER_COUNTRY_STORE))
																 .withValueSerde(Serdes.Long())
																 .withKeySerde(Serdes.String()));
	}

}
