FROM openjdk:8-jdk-alpine

ARG JAR_FILE

COPY ${JAR_FILE} sample-java-external-callout-service-1-jar-with-dependencies.jar

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/sample-java-external-callout-service-1-jar-with-dependencies.jar"]
