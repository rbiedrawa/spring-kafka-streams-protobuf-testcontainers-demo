package com.rbiedrawa.app.api;

import java.util.Optional;

import com.rbiedrawa.app.proto.accounts.Account;
import com.rbiedrawa.app.proto.accounts.CreateAccountRequest;

public interface AccountService {

	Account createNew(CreateAccountRequest createAccountRequest);

	Optional<Account> findById(String accountId);

	Long countByCountryCode(String countryCode);

}
