package com.rbiedrawa.app;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import com.rbiedrawa.app.containers.AbstractContainerBaseIT;

import org.junit.jupiter.api.Tag;

@Tag("integration")
@ActiveProfiles("test")
@SpringBootTest
public abstract class AbstractBaseIT extends AbstractContainerBaseIT {

	@DynamicPropertySource
	static void dynamicProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.kafka.bootstrap-servers", AbstractContainerBaseIT::getBootstrapServers);
		registry.add("spring.kafka.properties.schema.registry.url", AbstractContainerBaseIT::getSchemaRegistryUrl);
	}

}
