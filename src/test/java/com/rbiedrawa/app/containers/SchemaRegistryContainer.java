package com.rbiedrawa.app.containers;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

public class SchemaRegistryContainer extends GenericContainer<SchemaRegistryContainer> {

    SchemaRegistryContainer(DockerImageName dockerImageName) {
        super(dockerImageName);
        withExposedPorts(8081);
        waitingFor(Wait.forHttp("/subjects").forStatusCode(200));
    }

    SchemaRegistryContainer withKafka(KafkaContainer kafka) {
        withNetwork(kafka.getNetwork());
        withEnv("SCHEMA_REGISTRY_HOST_NAME", "schema-registry");
        withEnv("SCHEMA_REGISTRY_LISTENERS", "http://0.0.0.0:8081");
        withEnv("SCHEMA_REGISTRY_KAFKASTORE_BOOTSTRAP_SERVERS", "PLAINTEXT://" + kafka.getNetworkAliases().get(0) + ":9092");
        return self();
    }

    String getSchemaRegistryUrl() {
        String containerIpAddress = getContainerIpAddress();
        Integer mappedPort = getMappedPort(8081);
        return String.format("http://%s:%d", containerIpAddress, mappedPort);
    }

}