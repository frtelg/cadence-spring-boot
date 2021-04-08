package com.frtelg.cadence.controller;

import com.frtelg.cadence.workflow.GreetingWorkflow;
import com.uber.cadence.WorkflowExecution;
import com.uber.cadence.client.WorkflowClient;
import lombok.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/workflow")
public class GreetingWorkflowController {

    private final WorkflowClient workflowClient;

    public GreetingWorkflowController(WorkflowClient workflowClient) {
        this.workflowClient = workflowClient;
    }

    @GetMapping
    public ResponseEntity<WorkflowResponse> startWorkflow() {
        GreetingWorkflow workflow = workflowClient.newWorkflowStub(GreetingWorkflow.class);
        WorkflowExecution execution = WorkflowClient.start(workflow::greet);
        WorkflowResponse responseBody = new WorkflowResponse(execution.getWorkflowId());

        return ResponseEntity.ok(responseBody);
    }

    @PutMapping("/{workflowId}/{name}")
    public ResponseEntity<WorkflowResponse> triggerWorkflow(@PathVariable String workflowId,
                                                            @PathVariable String name) {
        GreetingWorkflow workflow = workflowClient.newWorkflowStub(GreetingWorkflow.class, workflowId);
        workflow.changeName(name);
        WorkflowResponse responseBody = new WorkflowResponse(workflowId);

        return ResponseEntity.ok(responseBody);
    }

    @GetMapping("{workflowId}/current-name")
    public ResponseEntity<NameFromWorkflowResponse> getCurrentName(@PathVariable String workflowId) {
        GreetingWorkflow workflow = workflowClient.newWorkflowStub(GreetingWorkflow.class, workflowId);
        String currentName = workflow.getCurrentName();
        NameFromWorkflowResponse responseBody = new NameFromWorkflowResponse(currentName, workflowId);

        return ResponseEntity.ok(responseBody);
    }

    @DeleteMapping("/{workflowId}")
    public ResponseEntity<WorkflowResponse> terminateWorkflow(@PathVariable String workflowId) {
        GreetingWorkflow workflow = workflowClient.newWorkflowStub(GreetingWorkflow.class, workflowId);
        workflow.terminate();

        WorkflowResponse responseBody = new WorkflowResponse(workflowId);

        return ResponseEntity.ok(responseBody);
    }

    @Value
    public static class WorkflowResponse {
        String workflowId;
    }

    @Value
    public static class NameFromWorkflowResponse {
        String name;
        String workflowId;
    }
}
