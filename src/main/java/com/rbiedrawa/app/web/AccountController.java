package com.rbiedrawa.app.web;

import static org.apache.kafka.streams.StoreQueryParameters.fromNameAndType;
import static org.apache.kafka.streams.state.QueryableStoreTypes.keyValueStore;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rbiedrawa.app.kafka.streams.AccountKStream;
import com.rbiedrawa.app.kafka.streams.AccountsPerCountryKStream;
import com.rbiedrawa.app.proto.accounts.Account;

import com.google.protobuf.util.JsonFormat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AccountController {

	@Qualifier(AccountKStream.STREAMS_BUILDER_BEAN_NAME)
	private final StreamsBuilderFactoryBean accountStreamsBuilder;

	@Qualifier(AccountsPerCountryKStream.STREAMS_BUILDER_BEAN_NAME)
	private final StreamsBuilderFactoryBean accountsPerCountryStreamsBuilder;

	@GetMapping(value = "/accounts/{accountId}", produces = MediaType.APPLICATION_JSON_VALUE)
	String account(@PathVariable String accountId) throws Exception {

		ReadOnlyKeyValueStore<String, Account> store = accountStreamsBuilder.getKafkaStreams()
																			.store(fromNameAndType(AccountKStream.ACCOUNT_STORE, keyValueStore()));
		Account account = store.get(accountId);
		log.info("Found account {}", account);

		// TODO: replace after this feature is https://github.com/spring-projects/spring-framework/issues/25457 created
		return JsonFormat.printer().print(account);
	}

	@GetMapping(value = "/statistics/accounts/{countryCode}", produces = MediaType.TEXT_PLAIN_VALUE)
	Long accountsPerCountry(@PathVariable String countryCode) throws Exception {
		ReadOnlyKeyValueStore<String, Long> store = accountsPerCountryStreamsBuilder.getKafkaStreams()
																					.store(fromNameAndType(AccountsPerCountryKStream.ACCOUNTS_PER_COUNTRY_STORE, keyValueStore()));
		return store.get(countryCode.toLowerCase());
	}
}
