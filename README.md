# interop-scheme-adapter

This project provides an API gateway to the ilp-service microservice. It supports methods to query, quote,payment request as specified below.

**Methods**: Query, Quote, Payment request, Invoices (create and get) to ILP-SERVICE API and receive a response via REST

**Reference**: [ReadMe file](https://github.com/LevelOneProject/ilp-service) in corresponding ILP project

Contents:

- [Deployment](#deployment)
- [Configuration](#configuration)
- [API](#api)
- [Logging](#logging)
- [Tests](#tests)

## Deployment

Project is built using Maven and uses Circle for Continous Integration.

### Installation and Setup

#### Anypoint Studio
* [https://www.mulesoft.com/platform/studio](https://www.mulesoft.com/platform/studio)
* Clone https://github.com/LevelOneProject/interop-scheme-adapter.git to local Git repository
* Import into Studio as a Maven-based Mule Project with pom.xml
* Go to Run -> Run As Configurations.  Make sure interop-scheme-adapter project is highlighted.

#### Standalone Mule ESB
* [https://developer.mulesoft.com/download-mule-esb-runtime](https://developer.mulesoft.com/download-mule-esb-runtime)
* Add the environment variable you are testing in (dev, prod, qa, etc).  Open <Mule Installation Directory>/conf/wrapper.conf and find the GC Settings section.  Here there will be a series of wrapper.java.additional.(n) properties.  create a new one after the last one where n=x (typically 14) and assign it the next number (i.e., wrapper.java.additional.15) and assign -DMULE_ENV=dev as its value (wrapper.java.additional.15=-DMULE_ENV=dev)
* Download the zipped project from Git
* Copy zipped file (Mule Archived Project) to <Mule Installation Directory>/apps

### Run Application

#### Anypoint Studio
* Run As Mule Application with Maven

#### Standalone Mule ESB
* CD to <Mule Installation Directory>/bin -> in terminal type ./mule

## Configuration

[pom.xml](./pom.xml) and [circle.yml](./circle.yml) can be found in the repo, also linked here

## API
[Endpoints Documentation](./API.md)

Below are the RAML and OpenAPI spec for reference

This is currently hosted as a service in the URL that looks like this:  http://\<awshost:port\>/spsp/client/v1/console/ , the OpenAPI docs and mule console details can be found [here](https://github.com/LevelOneProject/Docs/tree/master/AWS/Infrastructure/PI4-QA-Env) and [here](https://github.com/LevelOneProject/Docs/tree/master/AWS/Infrastructure/PI4-Test-Env)

* RAML [here](./src/main/api/scheme-adapter.raml)
* OpenAPI [here](./src/main/resources/documentation/dist/scheme-adapter.yaml)

## Logging

Server path to logs is: <mule_home>/logs/interop-scheme-adapter.log for example: /opt/mule/mule-dfsp1/logs/interop-scheme-adapter.log

Currently the logs are operational and include information such as TraceID and other details related to the calls or transactions such as path, method used, header information and payer/payee details.

## Tests

Java Unit Test exist for the project and include test for:

* Invalid path should return 404
* Query
* Payment

#### Anypoint Studio
* Run Unit Tests
* Test API with Anypoint Studio in APIKit Console
* Verify Responses in Studio Console output

Tests are run as part of executing the Maven pom.xml as mvn clean package. Also, tests can be run by running com.l1p.interop.spsp.SpspClientProxyFunctionalTest java class as JUnit Test.
