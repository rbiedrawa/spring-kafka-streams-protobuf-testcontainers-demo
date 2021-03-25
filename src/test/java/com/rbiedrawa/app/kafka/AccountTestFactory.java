package com.rbiedrawa.app.kafka;

import java.time.Instant;
import java.util.UUID;

import com.rbiedrawa.app.proto.accounts.Account;
import com.rbiedrawa.app.proto.accounts.AccountType;

import com.github.javafaker.Faker;
import com.google.protobuf.Timestamp;

public interface AccountTestFactory {

	static Account randomAccount() {
		Account.Builder accountBuilder = fakeAccount();
		return accountBuilder.build();
	}

	static Account randomAccount(String countryCode) {
		return fakeAccount().setCountryCode(countryCode)
							.build();
	}

	private static Account.Builder fakeAccount() {
		Faker faker = new Faker();
		Instant now = Instant.now();
		return Account.newBuilder()
					  .setId(UUID.randomUUID().toString())
					  .setCountryCode(faker.country().countryCode2())
					  .setEmail(faker.internet().emailAddress())
					  .setType(AccountType.PREMIUM)
					  .setCreatedDate(Timestamp.newBuilder().setSeconds(now.getEpochSecond()).setNanos(now.getNano()).build());
	}
}