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

class AccountKStreamTest {

	private final KafkaProtobufSerde<Account> accountSerde = TestSerdes.from(Account.class);
	private final Serde<String> keySerde = Serdes.String();

	private TopologyTestDriver testDriver;
	private TestInputTopic<String, Account> accountInputTopic;
	private KeyValueStore<String, Account> accountStateStore;

	@BeforeEach
	void setup() {
		testDriver = TopologyTestDriverFactory.create(sb -> new AccountKStream(accountSerde).accountAggregatorStream(sb));

		accountInputTopic = testDriver.createInputTopic(TOPIC_ACCOUNT_EVENTS, keySerde.serializer(), accountSerde.serializer());
		accountStateStore = testDriver.getKeyValueStore(AccountKStream.ACCOUNT_STORE);
	}

	@Test
	void should_materialize_account_when_account_event_received() {
		// Given
		var account = AccountTestFactory.randomAccount();

		// When
		accountInputTopic.pipeInput(account.getId(), account);

		// Then
		assertThat(accountStateStore.get(account.getId())).isEqualTo(account);
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