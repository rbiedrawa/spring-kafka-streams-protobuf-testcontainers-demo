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

```shell

# 1) List containers.
docker-compose -f ./deployment/docker/demo.yml ps

# 2) Check 'account-service' logs
docker-compose -f ./deployment/docker/demo.yml logs -f account-service
```