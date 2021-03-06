package com.rbiedrawa.app.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.rbiedrawa.app.kafka.config.KafkaTopics;
import com.rbiedrawa.app.proto.accounts.Account;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;

@Slf4j
@Component
public class AccountListener {

	@KafkaListener(topics = KafkaTopics.ACCOUNTS, groupId = "accounts-console-logger")
	public void consume(ConsumerRecord<String, Account> record) {
		log.info("----> Consumed {}", record.value());
	}
}
