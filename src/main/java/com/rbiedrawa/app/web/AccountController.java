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

import com.rbiedrawa.app.kafka.AccountKStream;
import com.rbiedrawa.app.kafka.config.KafkaConfiguration;
import com.rbiedrawa.app.proto.accounts.Account;

import com.google.protobuf.util.JsonFormat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;

@Slf4j
@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

	@Qualifier(AccountKStream.STREAMS_BUILDER_BEAN_NAME)
	private final StreamsBuilderFactoryBean streamsBuilder;

	@GetMapping(value = "{accountId}", produces = MediaType.APPLICATION_JSON_VALUE)
	String account(@PathVariable String accountId) throws Exception {

		ReadOnlyKeyValueStore<String, Account> store = streamsBuilder.getKafkaStreams()
																	 .store(fromNameAndType(KafkaConfiguration.ACCOUNT_STORE, keyValueStore()));
		Account account = store.get(accountId);
		log.info("Found account {}", account);

		// TODO: replace after this feature is https://github.com/spring-projects/spring-framework/issues/25457 created
		return JsonFormat.printer().print(account);
	}
}
