package com.rbiedrawa.app.kafka;

import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import com.rbiedrawa.app.IntegrationTest;
import com.rbiedrawa.app.api.AccountService;
import com.rbiedrawa.app.kafka.config.KafkaTopics;
import com.rbiedrawa.app.proto.accounts.Account;
import com.rbiedrawa.app.proto.accounts.AccountType;
import com.rbiedrawa.app.proto.accounts.CreateAccountRequest;

import io.confluent.kafka.streams.serdes.protobuf.KafkaProtobufSerde;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AccountKafkaServiceTest extends IntegrationTest {

	@Autowired
	private KafkaProtobufSerde<Account> accountSerde;

	@Autowired
	private AccountService accountService;

	private BlockingQueue<ConsumerRecord<String, Account>> records;

	private KafkaMessageListenerContainer<String, Account> container;

	@BeforeEach
	void setUp() {
		Map<String, Object> configs = new HashMap<>(KafkaTestUtils.consumerProps(getBootstrapServers(), this.getClass().getSimpleName(), "false"));
		DefaultKafkaConsumerFactory<String, Account> consumerFactory = new DefaultKafkaConsumerFactory<>(configs, new StringDeserializer(), accountSerde.deserializer());
		container = new KafkaMessageListenerContainer<>(consumerFactory, new ContainerProperties(KafkaTopics.ACCOUNTS));
		records = new LinkedBlockingQueue<>();
		container.setupMessageListener((MessageListener<String, Account>) records::add);
		container.start();
		ContainerTestUtils.waitForAssignment(container, KafkaTopics.DEFAULT_PARTITION_COUNT);
	}

	@AfterEach
	void tearDown() {
		container.stop();
	}

	@Test
	void should_create_account() throws Exception {
		// Given
		var newAccountRequest = CreateAccountRequest.newBuilder()
													.setEmail("test@test.com")
													.setCountryCode("GB")
													.setType(AccountType.FREE)
													.build();

		// When
		Account newAccount = accountService.createNew(newAccountRequest);

		// Awaitility.await().atMost(10, TimeUnit.SECONDS).until(() -> !records.isEmpty());
		// var record = records.poll(50, TimeUnit.MILLISECONDS);

		// Then
		var record = records.poll(5, TimeUnit.SECONDS);

		assertThat(record).isNotNull();
		assertThat(record.key()).isEqualTo(newAccount.getId());
		assertThat(record.value()).isEqualTo(newAccount);
	}

}