# cadence-spring-boot
An example Cadence application with Spring Boot

### How to use
Run `docker-compose up` to create a local Cadence deployment.
Then start CadenceApplication.

- Workflow can be started using REST:
  GET http://localhost:8081/workflow (returns the workflowId) `curl http://localhost:8081/workflow -H "Accept: application/json"`
- Trigger @SignalMethod changeName:
  PUT http://localhost:8081/workflow/{workflowId}/{newName} `curl -X PUT http://localhost:8081/workflow/{workflowId}/{newName} -H 'Content-Type: application/json'`
- Trigger @SignalMethod terminate:
  DELETE http://localhost:8081/workflow/{workflowId} `curl -X DELETE http://localhost:8081/workflow/{workflowId} -H 'Content-Type: application/json'`
- Trigger @QueryMethod getCurrentName:
  GET http://localhost:8081/workflow/{workflowId}/current-name `curl -X GET http://localhost:8081/workflow/{workflowId}/current-name -H 'Content-Type: application/json'`
- Cadence GUI can be accessed through http://localhost:8088