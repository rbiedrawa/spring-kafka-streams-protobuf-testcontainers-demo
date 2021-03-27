# Spring, Kafka Streams, gRPC with TestContainers - demo application

This demo was build for showcasing how to run multiple 'Kafka Streams' instances (with different consumer groups) inside single Spring Boot application.

Patterns, technologies, concepts demonstrated here:

* Testcontainers for Kafka streams integration tests.
* Multiple KStreams inside single boot application.
* Kafka protobuf integration.  
* Dynamic property binding in Spring Framework.
* gRPC for handling commands (CreateAccount).
* gRPC client via BloomRPC application.
* Interactive queries (kafka state store integration).
* Query endpoints for account details and statistics.
* REST endpoints for printing kstream topology details.
* Separating unit and integration test with gradle.
* Docker, docker compose integration.
* Custom gradle task for printing docker image name and sample docker-compose file.
* Faker with scheduling setup for generating dummy data, activated by `dummy_data_gen` spring profile.
* Custom lombok configuration.

## Table of Content

## Architecture

![architecture-diagram](./_docs/img/architecture.png)

## Getting Started

### Prerequisite

* Java 11
* Docker
* Kafka
* Schema Registry

### Installation

#### Local

* Start **Kafka broker** and **Schema Registry** using `Confluent Community` or `Confluent Platform` components.
    * Confluent community:
        ```shell
          docker-compose -f ./docker/local/docker-compose.yml up -d
        ```
    * Confluent Platform:
      ```shell
      docker-compose -f ./docker/confluent-platform/docker-compose.yml up -d
       ```
* Check if all containers started, using `docker-compose ps` command.
     ```shell
     # Sample output for Confluent Platform
         Name                  Command            State                       Ports                     
    ----------------------------------------------------------------------------------------------------
    broker            /etc/confluent/docker/run   Up      0.0.0.0:9092->9092/tcp, 0.0.0.0:9101->9101/tcp
    control-center    /etc/confluent/docker/run   Up      0.0.0.0:9021->9021/tcp                        
    rest-proxy        /etc/confluent/docker/run   Up      0.0.0.0:8082->8082/tcp                        
    schema-registry   /etc/confluent/docker/run   Up      0.0.0.0:8081->8081/tcp                        
    zookeeper         /etc/confluent/docker/run   Up      0.0.0.0:2181->2181/tcp, 2888/tcp, 3888/tcp    
     ```

* Start **Account Service**

  ```shell
  ./gradlew bootRun
  ```

### Usage

* Check **Create Account** flow
    * Import proto files into [BloomRPC](https://github.com/uw-labs/bloomrpc) client.
    * Choose **6565** as gRPC server port.
    * Create new account using *AccountService.Create* rpc call.
      ```shell
      # Sample request body
      {
        "email": "newAccount@test.com",
        "countryCode": "GB",
        "type": 2
      }
      ```
    * Get account id from response
      ```shell
      # Sample response body
      {
        "id": "519faeb7-ccdf-49dd-95a4-3e2a6ac8ad13",
        "email": "newAccount@test.com",
        "countryCode": "GB",
        "type": "PREMIUM",
        "createdDate": {
        "seconds": "1616860904",
        "nanos": 644947000
        }
      }
      ```
* Test interactive queries using REST endpoints.
    * Get account details from `accounts.store` state store:
      ```shell
        curl -X GET --location "http://localhost:8080/api/v1/accounts/519faeb7-ccdf-49dd-95a4-3e2a6ac8ad13"
      ```
    * Get account statistics (accounts per country):
      ```shell
      curl -X GET --location "http://localhost:8080/api/v1/statistics/accounts/GB"
      ```

* Analise kafka streams topology:
    * Get all available topologies.
      ```shell
      curl -X GET --location "http://localhost:8080/topologies"
      ```  
    * Print topology for **accounts-per-country** kstream.
      ```shell
      curl -X GET --location "http://localhost:8080/topologies/accounts-per-country"
      ```    
    * Print topology diagram, visit https://zz85.github.io/kafka-streams-viz/ page.

### Testing Kafka Stream

* Unit Test (TopologyTestDriver) examples may be found under [src/test/](./src/test/java/com/rbiedrawa/app/kafka/streams).
* Integration Test (Testcontainers) examples may be found under [src/integrationTest/](./src/integrationTest/java/com/rbiedrawa/app).

## References

* [Kafka Streams Topology Visualizer](https://github.com/zz85/kafka-streams-viz)  
* [Spring for Apache Kafka](https://docs.spring.io/spring-boot/docs/2.4.4/reference/htmlsingle/#boot-features-kafka)
* [Quick Start for Apache Kafka using Confluent Platform (Docker)](https://docs.confluent.io/platform/current/quickstart/ce-docker-quickstart.html)
* [DynamicPropertySource in Spring Framework](https://spring.io/blog/2020/03/27/dynamicpropertysource-in-spring-framework-5-2-5-and-spring-boot-2-2-6)
* [Testcontainers: singleton-containers](https://www.testcontainers.org/test_framework_integration/manual_lifecycle_control/#singleton-containers)
* [Testcontainers: Kafka Modules Reference Guide](https://www.testcontainers.org/modules/kafka/)
* [Testing a streams application](https://kafka.apache.org/11/documentation/streams/developer-guide/testing.html)
* [BloomRPC](https://github.com/uw-labs/bloomrpc)
* [Java Faker](https://github.com/DiUS/java-faker)
* [Gradle Test Logger Plugin](https://github.com/radarsh/gradle-test-logger-plugin)
* [Protobuf Plugin for Gradle](https://github.com/google/protobuf-gradle-plugin)

## License

Distributed under the MIT License. See `LICENSE` for more information.
