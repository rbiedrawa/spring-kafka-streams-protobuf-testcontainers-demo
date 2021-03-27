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
import org.junit.jupiter.api.Test;

class AccountKStreamTryCatchTest {

	private final KafkaProtobufSerde<Account> accountSerde = TestSerdes.from(Account.class);
	private final Serde<String> keySerde = Serdes.String();

	@Test
	void should_materialize_account_when_account_event_received_using_try_catch_block() {
		try (var testDriver = TopologyTestDriverFactory.create(sb -> new AccountKStream(accountSerde).accountAggregatorStream(sb))) {
			// Given
			var accountInputTopic = testDriver.createInputTopic(TOPIC_ACCOUNT_EVENTS, keySerde.serializer(), accountSerde.serializer());
			var accountStateStore = testDriver.getKeyValueStore(AccountKStream.ACCOUNT_STORE);

			var account = AccountTestFactory.randomAccount();

			// When
			accountInputTopic.pipeInput(account.getId(), account);

			// Then
			assertThat(accountStateStore.get(account.getId())).isEqualTo(account);
		}
	}
}