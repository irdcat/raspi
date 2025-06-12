FROM arm64v8/openjdk:21-jdk

RUN groupadd apigw
RUN useradd -g apigw apigw
USER apigw:apigw
WORKDIR /home/apigw/api-gateway
ARG JAR_FILE=api-gateway.jar
COPY ${JAR_FILE} server.jar