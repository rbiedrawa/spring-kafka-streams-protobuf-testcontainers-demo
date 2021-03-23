FROM openjdk:11-jre-slim
RUN useradd -ms /bin/bash spring-boot-app
USER spring-boot-app:spring-boot-app
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]