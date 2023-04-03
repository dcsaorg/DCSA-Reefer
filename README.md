DCSA-Reefer-Commercial
==============================================================================================

**[RECOMMENDED]**
Setup a Github Personal Access Token as mentioned [here](https://github.com/dcsaorg/DCSA-Core/blob/master/README.md#how-to-use-dcsa-core-packages).


## Build and run using using docker-compose

1) Build and run with
```
mvn -U clean package
docker-compose up -d -V --build
```

2) Verify that the application is running,
```
curl http://localhost:9090/actuator/health
```

(the database is automatically included when running with docker)


## Building and running manually/locally

Set up a postgresql database with credentials or run it in docker with
```
docker compose up -d dcsa-test-db
```

Then build and run the application with

```
mvn -U clean package
mvn -pl reefer-commercial-application -am spring-boot:run
```


## Running from inside an IDE

Set up a postgresql database with credentials or run it in docker with
```
docker compose up -d dcsa-test-db
```

Then make sure you modify the run environment for the ```Application``` class so it includes the following environment
variable
```
SPRING_PROFILES_ACTIVE=dev
```
Then just start the ```Application``` class.


## Branching and versioning

The branching and devopment model is described
[here](https://dcsa.atlassian.net/wiki/spaces/DDT/pages/71204878/Development+flow+and+CI).


## Security considerations

This reference implementation does not do any authentication/authorization and should not be used
in production as is. Using this as is in production would expose data for all parties to all other
parties without checking whether they should have access.
