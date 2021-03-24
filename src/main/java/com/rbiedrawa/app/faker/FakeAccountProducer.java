package com.rbiedrawa.app.faker;

import java.time.Instant;
import java.util.UUID;
import java.util.stream.IntStream;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.rbiedrawa.app.kafka.config.KafkaConfiguration;
import com.rbiedrawa.app.proto.accounts.Account;
import com.rbiedrawa.app.proto.accounts.AccountType;

import com.github.javafaker.Faker;
import com.google.protobuf.Timestamp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class FakeAccountProducer {
	private final KafkaTemplate<String, Account> kafkaTemplate;

	@Scheduled(fixedRate = 5_000)
	public void produceAccounts() {
		IntStream.range(0, 5).forEach(value -> {
			var account = randomAccount();
			kafkaTemplate.send(KafkaConfiguration.TOPIC_ACCOUNT_EVENTS, account.getId(), account);
			log.info("----> Generated fake account {}", account);
		});
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
