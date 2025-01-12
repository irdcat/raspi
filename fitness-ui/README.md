# fitness-ui

Sub project containing everything related to the fitness-ui deployment used on the clster.

## Structure

```
.
├── helm           # Helm Chart of a mongo deployment
├── public         # Static files used by the application
├── src            # Source of the application
```

## Local Development

Before running application locally for the first time node dependencies have to be installed, using command below:

```
npm install
```

Then application can be started by using following command:

```
npm start
```

### Server

Even locally this component relies on a real server to be up and running and listening on the port 8080. To run the server, refer to the instructions in [fitness-server README.md](../fitness-server/README.md)

## Helm Chart

### Values

#### resources.requests.memory

**Default**: `500Mi`

Controls how much memory is initially requested by this application. Value is expressed as [Quantity](https://kubernetes.io/docs/reference/kubernetes-api/common-definitions/quantity/#Quantity)

#### resoures.requests.cpu

**Default**: `0.5`

Controls how many CPUs are initially requested by this application. Value is expressed as [Quantity](https://kubernetes.io/docs/reference/kubernetes-api/common-definitions/quantity/#Quantity)