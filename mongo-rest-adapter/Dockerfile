FROM arm64v8/openjdk:21-jdk

WORKDIR adapter
RUN groupadd adapter
RUN useradd -g adapter adapter
USER adapter:adapter
ARG JAR_FILE=mongo-rest-adapter.jar
COPY ${JAR_FILE} server.jar