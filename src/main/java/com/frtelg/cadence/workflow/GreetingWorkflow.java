package com.frtelg.cadence.workflow;

import com.uber.cadence.workflow.QueryMethod;
import com.uber.cadence.workflow.SignalMethod;
import com.uber.cadence.workflow.WorkflowMethod;

public interface GreetingWorkflow {
    String TASK_LIST = "Example";
    // A workflow method describes the main workflow. Calling the workflow will start the workflow, the workflow ends
    // when the WorkflowMethod is complete
    @WorkflowMethod(executionStartToCloseTimeoutSeconds = 360, taskList = TASK_LIST)
    void greet();

    // A signal method can be used to trigger the workflow from outside, in order to change its state.
    // Return type should always be void
    @SignalMethod
    void changeName(String name);

    @SignalMethod
    void terminate();

    // A query method can be used to get workflow state
    @QueryMethod
    String getCurrentName();
}