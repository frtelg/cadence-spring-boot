package com.frtelg.cadence.config;

import com.uber.cadence.activity.ActivityOptions;
import com.uber.cadence.client.WorkflowClient;
import com.uber.cadence.client.WorkflowClientOptions;
import com.uber.cadence.common.RetryOptions;
import com.uber.cadence.serviceclient.ClientOptions;
import com.uber.cadence.serviceclient.IWorkflowService;
import com.uber.cadence.serviceclient.WorkflowServiceTChannel;
import com.uber.cadence.worker.WorkerFactory;
import com.uber.cadence.worker.WorkerFactoryOptions;
import com.uber.cadence.worker.WorkerOptions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

@Configuration
public class CadenceConfiguration {

    static final String DOMAIN = "test-domain";

    private static final RetryOptions RETRY_OPTIONS = new RetryOptions.Builder().setMaximumAttempts(3)
            .setInitialInterval(Duration.of(3, ChronoUnit.SECONDS))
            .setBackoffCoefficient(2)
            .setMaximumInterval(Duration.of(60, ChronoUnit.SECONDS))
            .build();
    public static final ActivityOptions ACTIVITY_OPTIONS = new ActivityOptions.Builder()
            .setScheduleToCloseTimeout(Duration.of(60, ChronoUnit.MINUTES))
            .setRetryOptions(RETRY_OPTIONS)
            .build();

    @Bean
    public WorkflowClientOptions workflowClientOptions() {
        return WorkflowClientOptions.newBuilder()
                .setDomain(DOMAIN)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean(WorkflowClient.class)
    public WorkflowClient workflowClient(IWorkflowService workflowService, WorkflowClientOptions workflowClientOptions) {
        return WorkflowClient.newInstance(workflowService, workflowClientOptions);
    }

    @Bean
    @ConditionalOnMissingBean(ClientOptions.class)
    public ClientOptions cadenceClientOptions() {
        return ClientOptions.newBuilder()
                .setClientAppName("testApp")
                .setHost("localhost")
                .setPort(7933)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean(IWorkflowService.class)
    public IWorkflowService cadenceClient(ClientOptions clientOptions) {
        return new WorkflowServiceTChannel(clientOptions);
    }

    @Bean
    public WorkerFactoryOptions workerFactoryOptions() {
        return WorkerFactoryOptions.newBuilder().build();
    }

    @Bean
    @ConditionalOnMissingBean(WorkerFactory.class)
    public WorkerFactory workerFactory(WorkflowClient workflowClient, WorkerFactoryOptions workerFactoryOptions) {
        return new WorkerFactory(workflowClient, workerFactoryOptions);
    }

    @Bean
    public WorkerOptions workerOptions() {
        return WorkerOptions.newBuilder().build();
    }
}
