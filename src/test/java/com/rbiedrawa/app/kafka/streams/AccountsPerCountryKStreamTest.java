package com.rbiedrawa.app.kafka.streams;

import static com.rbiedrawa.app.kafka.config.KafkaConfiguration.TOPIC_ACCOUNT_EVENTS;
import static org.assertj.core.api.Assertions.*;

import com.rbiedrawa.app.kafka.AccountTestFactory;
import com.rbiedrawa.app.kafka.utils.TestSerdes;
import com.rbiedrawa.app.kafka.utils.TopologyTestDriverFactory;
import com.rbiedrawa.app.proto.accounts.Account;

import io.confluent.kafka.streams.serdes.protobuf.KafkaProtobufSerde;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.state.KeyValueStore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AccountsPerCountryKStreamTest {
	private final KafkaProtobufSerde<Account> accountSerde = TestSerdes.from(Account.class);
	private final Serde<String> keySerde = Serdes.String();

	private TopologyTestDriver testDriver;
	private TestInputTopic<String, Account> accountInputTopic;
	private KeyValueStore<String, Long> accountsPerCountryStore;

	@BeforeEach
	void init() {
		testDriver = TopologyTestDriverFactory.create(streamsBuilder ->
														  new AccountsPerCountryKStream(accountSerde).accountsPerCountryStream(streamsBuilder));

		accountInputTopic = testDriver.createInputTopic(TOPIC_ACCOUNT_EVENTS, keySerde.serializer(), accountSerde.serializer());
		accountsPerCountryStore = testDriver.getKeyValueStore(AccountsPerCountryKStream.ACCOUNTS_PER_COUNTRY_STORE);
	}

	@Test
	void should_counts_account_per_country() {
		// Given
		var gb = "gb";
		var us = "us";

		// When
		accountInputTopic.pipeInput("ignored", AccountTestFactory.randomAccount(gb));
		accountInputTopic.pipeInput("ignored", AccountTestFactory.randomAccount(gb));
		accountInputTopic.pipeInput("ignored", AccountTestFactory.randomAccount(us));

		// Then
		assertThat(accountsPerCountryStore.get(gb)).isEqualTo(2L);
		assertThat(accountsPerCountryStore.get(us)).isEqualTo(1L);
	}

	@AfterEach
	void tearDown() {
		try {
			this.testDriver.close();
		} catch (final RuntimeException ex) {
			// https://issues.apache.org/jira/browse/KAFKA-6647
		}
	}
}