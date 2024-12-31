FROM arm64v8/openjdk:21-jdk

WORKDIR fitness
RUN groupadd fitness
RUN useradd -g fitness fitness
USER fitness:fitness
ARG JAR_FILE=fitness.jar
COPY ${JAR_FILE} server.jar