# Demo Materials

## Model creation/export (via Databricks notebook)
You can create a free [Community Edition](https://databricks.com/try-databricks) account with Databricks, and then follow the [instructions](https://docs.databricks.com/user-guide/notebooks/index.html#import-notebook) to import the _Export Model.dbc_ notebook into your environment.

## Local microservice (via Lagom)
After executing the Databricks notebook (and downloading your model JAR), then update the local path to the model file within the _FraudmodelServiceImpl_ class. Alternatively, you can find a local copy of the model JAR in the resources folder.

Once you've done this, then build and run the project using the SBT command below.

./sbt runAll

## Service endpoints
After running the local microservice, you can reach the endpoints below.

### Model metadata
GET<br>
http://localhost:9000/api/fraud

### Analyze record
POST<br>
http://localhost:9000/api/fraud/analyze

Content-Type<br>
application/json

Body<br>
{"RegisteredDevice":1,"Flow":"hacker-portal","Action":"BALANCE_INQUIRY","Amount":0}
