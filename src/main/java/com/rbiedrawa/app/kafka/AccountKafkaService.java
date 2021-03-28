package com.rbiedrawa.app.kafka;

import static org.apache.kafka.streams.StoreQueryParameters.fromNameAndType;
import static org.apache.kafka.streams.state.QueryableStoreTypes.keyValueStore;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.rbiedrawa.app.api.AccountFactory;
import com.rbiedrawa.app.api.AccountService;
import com.rbiedrawa.app.kafka.config.KafkaTopics;
import com.rbiedrawa.app.kafka.streams.AccountKStream;
import com.rbiedrawa.app.kafka.streams.AccountsPerCountryKStream;
import com.rbiedrawa.app.proto.accounts.Account;
import com.rbiedrawa.app.proto.accounts.CreateAccountRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountKafkaService implements AccountService {

	@Qualifier(AccountKStream.STREAMS_BUILDER_BEAN_NAME)
	private final StreamsBuilderFactoryBean accountStreamsBuilder;

	@Qualifier(AccountsPerCountryKStream.STREAMS_BUILDER_BEAN_NAME)
	private final StreamsBuilderFactoryBean accountsPerCountryStreamsBuilder;

	private final KafkaTemplate<String, Account> kafkaTemplate;

	@Override
	public Account createNew(CreateAccountRequest createAccountRequest) {
		var newAccount = AccountFactory.from(createAccountRequest);
		kafkaTemplate.send(KafkaTopics.ACCOUNTS, newAccount.getId(), newAccount);
		return newAccount;
	}

	@Override
	public Optional<Account> findById(String accountId) {
		log.info("Find by account by id: {}", accountId);
		ReadOnlyKeyValueStore<String, Account> store = accountStreamsBuilder.getKafkaStreams()
																			.store(fromNameAndType(AccountKStream.ACCOUNT_STORE, keyValueStore()));
		return Optional.ofNullable(store.get(accountId));
	}

	@Override
	public Long countByCountryCode(String countryCode) {
		log.info("Count by countryCode: {}", countryCode);
		ReadOnlyKeyValueStore<String, Long> store = accountsPerCountryStreamsBuilder.getKafkaStreams()
																					.store(fromNameAndType(AccountsPerCountryKStream.ACCOUNTS_PER_COUNTRY_STORE, keyValueStore()));
		return Optional.ofNullable(store.get(countryCode.toLowerCase())).orElse(0L);
	}

}
