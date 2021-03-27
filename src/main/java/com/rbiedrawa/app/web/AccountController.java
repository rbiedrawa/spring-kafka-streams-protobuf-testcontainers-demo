package com.rbiedrawa.app.web;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rbiedrawa.app.api.AccountService;
import com.rbiedrawa.app.proto.accounts.Account;

import com.google.protobuf.util.JsonFormat;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AccountController {

	private final AccountService accountService;

	@GetMapping(value = "/accounts/{accountId}", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<String> account(@PathVariable String accountId) {
		return accountService.findById(accountId)
							 .map(this::toJson)
							 .map(ResponseEntity::ok)
							 .orElseGet(() -> ResponseEntity.notFound().build());
	}

	@GetMapping(value = "/statistics/accounts/{countryCode}", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Map<String, Long>> accountsPerCountry(@PathVariable String countryCode) {
		return ResponseEntity.ok(Map.of("count", accountService.countByCountryCode(countryCode)));
	}

	@SneakyThrows
	private String toJson(Account account) {
		// TODO: replace after this issue is https://github.com/spring-projects/spring-framework/issues/25457 closed
		// or implement Json DTO
		return JsonFormat.printer().print(account);
	}
}
