package com.rbiedrawa.app.kafka.streams;

import static org.assertj.core.api.Assertions.*;

import com.rbiedrawa.app.kafka.AccountTestFactory;
import com.rbiedrawa.app.kafka.config.KafkaTopics;
import com.rbiedrawa.app.kafka.utils.TestSerdes;
import com.rbiedrawa.app.kafka.utils.TopologyTestDriverFactory;
import com.rbiedrawa.app.proto.accounts.Account;

import io.confluent.kafka.streams.serdes.protobuf.KafkaProtobufSerde;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.junit.jupiter.api.Test;

class AccountsPerCountryKStreamTest {
	private final KafkaProtobufSerde<Account> accountSerde = TestSerdes.from(Account.class);
	private final Serde<String> keySerde = Serdes.String();

	@Test
	void should_counts_account_per_country() {
		try (var testDriver = TopologyTestDriverFactory.create(streamsBuilder ->
																   new AccountsPerCountryKStream(accountSerde).accountsPerCountryStream(streamsBuilder))) {

			// Given
			var accountInputTopic = testDriver.createInputTopic(KafkaTopics.ACCOUNTS, keySerde.serializer(), accountSerde.serializer());
			var accountsPerCountryStore = testDriver.getKeyValueStore(AccountsPerCountryKStream.ACCOUNTS_PER_COUNTRY_STORE);


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
	}
}