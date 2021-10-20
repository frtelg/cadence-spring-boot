package com.frtelg.cadence.configuration;

import com.uber.cadence.client.WorkflowClient;
import com.uber.cadence.client.WorkflowClientOptions;
import com.uber.cadence.serviceclient.IWorkflowService;
import com.uber.cadence.testing.TestEnvironmentOptions;
import com.uber.cadence.testing.TestWorkflowEnvironment;
import com.uber.cadence.worker.WorkerFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import javax.annotation.PreDestroy;

@TestConfiguration
public class CadenceTestServer {
    private final TestWorkflowEnvironment testWorkflowEnvironment;

    public CadenceTestServer(WorkflowClientOptions workflowClientOptions) {
        var options = new TestEnvironmentOptions.Builder()
                .setWorkflowClientOptions(workflowClientOptions)
                .build();
        this.testWorkflowEnvironment = TestWorkflowEnvironment.newInstance(options);
    }

    @Bean
    public WorkflowClient testWorkflowClient(WorkflowClientOptions workflowClientOptions) {
        return testWorkflowEnvironment.newWorkflowClient(workflowClientOptions);
    }

    @Bean
    public IWorkflowService cadenceTestService() {
        return testWorkflowEnvironment.getWorkflowService();
    }

    @Bean
    public TestWorkflowEnvironment testWorkflowEnvironment() {
        return testWorkflowEnvironment;
    }

    @Bean
    public WorkerFactory testWorkerFactory() {
        return testWorkflowEnvironment.getWorkerFactory();
    }

    @PreDestroy
    public void closeWorkflowService() {
        testWorkflowEnvironment.close();
    }
}