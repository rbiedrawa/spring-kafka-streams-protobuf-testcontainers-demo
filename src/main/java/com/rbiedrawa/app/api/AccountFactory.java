package com.rbiedrawa.app.api;

import java.time.Instant;
import java.util.UUID;

import com.rbiedrawa.app.proto.accounts.Account;
import com.rbiedrawa.app.proto.accounts.CreateAccountRequest;

import com.google.protobuf.Timestamp;

public interface AccountFactory {

	static Account from(CreateAccountRequest accountRequest) {
		Instant now = Instant.now();
		String accountId = UUID.randomUUID().toString();
		return Account.newBuilder()
					  .setId(accountId)
					  .setCountryCode(accountRequest.getCountryCode())
					  .setEmail(accountRequest.getEmail())
					  .setType(accountRequest.getType())
					  .setCreatedDate(Timestamp.newBuilder().setSeconds(now.getEpochSecond()).setNanos(now.getNano()).build())
					  .build();
	}

}
