package com.rbiedrawa.app.grpc;

import com.rbiedrawa.app.api.AccountService;
import com.rbiedrawa.app.proto.accounts.Account;
import com.rbiedrawa.app.proto.accounts.AccountServiceGrpc;
import com.rbiedrawa.app.proto.accounts.CreateAccountRequest;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lognet.springboot.grpc.GRpcService;

@Slf4j
@GRpcService
@RequiredArgsConstructor
public class AccountServiceRpc extends AccountServiceGrpc.AccountServiceImplBase {

	private final AccountService accountService;

	@Override
	public void create(CreateAccountRequest request, StreamObserver<Account> responseObserver) {
		log.info("Requested account creation using rpc call, request {}", request);
		Account newAccount = accountService.createNew(request);
		responseObserver.onNext(newAccount);
		responseObserver.onCompleted();
	}
}
