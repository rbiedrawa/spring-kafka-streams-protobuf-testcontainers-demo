# Docker Deployment 

## Overview

Instructions how to run demo inside docker.

## Getting Started

### Prerequisite

Dockerize spring boot application:
```shell
./gradlew bootBuildImage
```

### Installation

```shell
# Run demo
docker-compose -f ./deployment/docker/demo.yml up -d
```

### Uninstallation

```shell
docker-compose -f ./deployment/docker/demo.yml down -v
```

### Useful commands

* List containers.
```shell
docker-compose -f ./deployment/docker/demo.yml ps

 # Sample output
     Name                  Command            State                                    Ports                                  
------------------------------------------------------------------------------------------------------------------------------
account-service   /cnb/process/web            Up      0.0.0.0:6565->6565/tcp, 0.0.0.0:8080->8080/tcp                          
kafka             /etc/confluent/docker/run   Up      0.0.0.0:29092->29092/tcp, 0.0.0.0:9092->9092/tcp, 0.0.0.0:9101->9101/tcp
schema-registry   /etc/confluent/docker/run   Up      0.0.0.0:8081->8081/tcp                                                  
zookeeper         /etc/confluent/docker/run   Up      0.0.0.0:2181->2181/tcp, 2888/tcp, 3888/tcp
```

* Check 'account-service' logs.
```shell
docker-compose -f ./deployment/docker/demo.yml logs -f account-service
```
