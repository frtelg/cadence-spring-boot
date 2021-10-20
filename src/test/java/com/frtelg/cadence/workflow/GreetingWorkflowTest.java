package com.frtelg.cadence.workflow;

import com.frtelg.cadence.activity.GreetingActivities;
import com.frtelg.cadence.activity.GreetingActivitiesImpl;
import com.uber.cadence.client.WorkflowClient;
import com.uber.cadence.testing.TestWorkflowEnvironment;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.PrintStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class GreetingWorkflowTest {
    private TestWorkflowEnvironment workflowEnvironment;
    private WorkflowClient workflowClient;
    private final Class<?> workflowImplementationClass = GreetingWorkflowImpl.class;
    private PrintStream printStream;

    @BeforeEach
    void setup() {
        printStream = mock(PrintStream.class);
        var greetingActivities = new GreetingActivitiesImpl(printStream);

        workflowEnvironment = TestWorkflowEnvironment.newInstance();
        var worker = workflowEnvironment.newWorker(GreetingWorkflow.TASK_LIST);
        worker.registerWorkflowImplementationTypes(workflowImplementationClass);
        worker.registerActivitiesImplementations(greetingActivities);

        workflowClient = workflowEnvironment.newWorkflowClient();

        workflowEnvironment.start();
    }

    @AfterEach
    void shutdown() {
        workflowEnvironment.close();
    }

    @Test
    void testWorkflow() throws InterruptedException, ExecutionException, TimeoutException {
        var workflow = workflowClient.newWorkflowStub(GreetingWorkflow.class);
        var expectedName = "Handige Harry";

        // Start workflow
        var execution = WorkflowClient.execute(workflow::greet);
        assertEquals("Stranger", workflow.getCurrentName());

        // Send signal
        workflow.changeName(expectedName);

        await().atMost(200, TimeUnit.MILLISECONDS)
                .until(() -> workflow.getCurrentName().equals(expectedName));

        var currentName = workflow.getCurrentName();
        assertEquals(expectedName, currentName);

        // Terminate workflow
        workflow.terminate();
        execution.get(50, TimeUnit.MILLISECONDS);

        assertTrue(execution.isDone());
        verify(printStream, times(1)).println("Hi Stranger");
        verify(printStream, times(1)).println("Hi Handige Harry");
    }
}
