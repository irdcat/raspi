# mongo

Sub project containing everything related to the mongo deployment used on the cluster.
This is not meant to be used locally. Any sub project that is dependent on mongo has it's own way of handling that dependency in a local environment.

## Structure

```
.
├── helm           # Helm Chart of a mongo deployment
```

## Local Development

This project is not intended to be used in a local environment

## Helm Chart

### Values

#### development

**Default**: `false`

Controls whether to initialize resources that meant to aid testing. Setting it to true will deploy `NodePort` service, see: [external-service.yaml](./helm/templates/external-service.yaml)