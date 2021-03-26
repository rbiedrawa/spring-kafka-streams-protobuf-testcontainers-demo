package com.rbiedrawa.app;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import com.rbiedrawa.app.containers.AbstractContainerBaseTest;

import org.junit.jupiter.api.Tag;

@Tag("integration")
@ActiveProfiles("integration-test")
@SpringBootTest
public abstract class IntegrationTest extends AbstractContainerBaseTest {

	@DynamicPropertySource
	static void dynamicProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.kafka.bootstrap-servers", AbstractContainerBaseTest::getBootstrapServers);
		registry.add("spring.kafka.properties.schema.registry.url", AbstractContainerBaseTest::getSchemaRegistryUrl);
	}

}
