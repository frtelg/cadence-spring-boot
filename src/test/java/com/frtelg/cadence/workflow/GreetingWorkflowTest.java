package com.frtelg.cadence.workflow;

import com.frtelg.cadence.activity.GreetingActivities;
import com.frtelg.cadence.activity.GreetingActivitiesImpl;
import com.uber.cadence.client.WorkflowClient;
import com.uber.cadence.testing.TestWorkflowEnvironment;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class GreetingWorkflowTest {
    private TestWorkflowEnvironment workflowEnvironment;
    private WorkflowClient workflowClient;
    private final Class<?> workflowImplementationClass = GreetingWorkflowImpl.class;
    private GreetingActivities greetingActivities;
    private PrintStream printStream;

    @BeforeEach
    void setup() {
        printStream = mock(PrintStream.class);
        greetingActivities = new GreetingActivitiesImpl(printStream);

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

        // Start workflow
        var execution = WorkflowClient.execute(workflow::greet);
        assertEquals("Stranger", workflow.getCurrentName());

        // Send signal
        workflow.changeName("Handige Harry");

        Thread.sleep(50); // to avoid timing issues
        var currentName = workflow.getCurrentName();
        assertEquals("Handige Harry", currentName);

        // Terminate workflow
        workflow.terminate();
        execution.get(50, TimeUnit.MILLISECONDS);

        assertTrue(execution.isDone());
        verify(printStream, times(1)).println("Hi Stranger");
        verify(printStream, times(1)).println("Hi Handige Harry");
    }
}
