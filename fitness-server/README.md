# fitness-server

Sub project containing everything related to the fitness-server deployment used on the cluster.

## Structure

```
.
├── helm           # Helm Chart of a mongo deployment
├── gradle         # Gradle utilities including the wrapper
├── src            # Source of the application
```

## Local Development

Although fitness-server is an SpringBoot application it is not run using classic `bootRun` task, but rather `bootTestRun`.
Test variant of the application utilizes TestContainers library to setup a container that runs mongo database for the lifetime of application.
Test variant of the application also bring it's own SpringApplication initializers that populate the DB with some test data.

To run the server locally use following command:

```
./gradlew bootTestRun
```

See: [TestFitnessApplication.kt](./src/test/kotlin/irdcat/fitness/TestFitnessApplication.kt)

## Helm Chart

### Values

#### resources.requests.memory

**Default**: `500Mi`

Controls how much memory is initially requested by this application. Value is expressed as [Quantity](https://kubernetes.io/docs/reference/kubernetes-api/common-definitions/quantity/#Quantity)

#### resoures.requests.cpu

**Default**: `0.5`

Controls how many CPUs are initially requested by this application. Value is expressed as [Quantity](https://kubernetes.io/docs/reference/kubernetes-api/common-definitions/quantity/#Quantity)

#### mongo.service

**Default**: `mongo-service`

Name of the ClusterIP service that exposes mongo port inside the cluster. It's name depends on mongo helm release name.

#### mongo.namespace

**Default**: `default`

Name of the kubernetes namespace to which mongo has been deployed.

#### mongo.port

**Default**: `27017`

Port on which MongoDB is exposed.

#### mongo.database

**Default**: `fitness`

Name of the database used by the application.