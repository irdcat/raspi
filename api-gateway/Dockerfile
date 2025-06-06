FROM arm64v8/openjdk:21-jdk

WORKDIR api-gateway
RUN groupadd apigw
RUN useradd -g apigw apigw
USER apigw:apigw
ARG JAR_FILE=api-gateway.jar
COPY ${JAR_FILE} server.jar