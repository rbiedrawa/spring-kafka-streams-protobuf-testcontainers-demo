package com.rbiedrawa.app.kafka;

import static com.rbiedrawa.app.kafka.config.KafkaConfiguration.TOPIC_ACCOUNT_EVENTS;
import static org.assertj.core.api.Assertions.*;

import java.time.Instant;
import java.util.UUID;

import com.rbiedrawa.app.kafka.config.KafkaConfiguration;
import com.rbiedrawa.app.kafka.utils.TestSerdes;
import com.rbiedrawa.app.kafka.utils.TopologyTestDriverFactory;
import com.rbiedrawa.app.proto.accounts.Account;
import com.rbiedrawa.app.proto.accounts.AccountType;

import com.github.javafaker.Faker;
import com.google.protobuf.Timestamp;
import io.confluent.kafka.streams.serdes.protobuf.KafkaProtobufSerde;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.state.KeyValueStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AccountKStreamTest {

	private final KafkaProtobufSerde<Account> accountSerde = TestSerdes.from(Account.class);
	private final Serde<String> keySerde = Serdes.String();

	private TestInputTopic<String, Account> accountInputTopic;
	private KeyValueStore<String, Account> accountStateStore;

	@BeforeEach
	void setup() {
		var testDriver = TopologyTestDriverFactory.create(streamsBuilder ->
															  new AccountKStream(accountSerde).accounts(streamsBuilder)
														 );

		accountInputTopic = testDriver.createInputTopic(TOPIC_ACCOUNT_EVENTS,
														keySerde.serializer(),
														accountSerde.serializer());

		accountStateStore = testDriver.getKeyValueStore(KafkaConfiguration.ACCOUNT_STORE);
	}

	@Test
	void should_materialize_account_when_account_event_received() {
		// Given
		var account = randomAccount();

		// When
		accountInputTopic.pipeInput(account.getId(), account);

		// Then
		assertThat(accountStateStore.get(account.getId())).isEqualTo(account);
	}


	private Account randomAccount() {
		Faker faker = new Faker();

		Instant now = Instant.now();

		return Account.newBuilder()
					  .setId(UUID.randomUUID().toString())
					  .setCountryCode(faker.country().countryCode2())
					  .setEmail(faker.internet().emailAddress())
					  .setType(AccountType.PREMIUM)
					  .setCreatedDate(Timestamp.newBuilder().setSeconds(now.getEpochSecond()).setNanos(now.getNano()).build())
					  .build();
	}

}