package com.frtelg.cadence.config;

import com.frtelg.cadence.activity.GreetingActivitiesImpl;
import com.frtelg.cadence.workflow.GreetingWorkflow;
import com.frtelg.cadence.workflow.GreetingWorkflowImpl;
import com.uber.cadence.DescribeDomainResponse;
import com.uber.cadence.ListDomainsRequest;
import com.uber.cadence.ListDomainsResponse;
import com.uber.cadence.RegisterDomainRequest;
import com.uber.cadence.serviceclient.IWorkflowService;
import com.uber.cadence.worker.Worker;
import com.uber.cadence.worker.WorkerFactory;
import com.uber.cadence.worker.WorkerOptions;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;

import static com.frtelg.cadence.config.CadenceConfiguration.DOMAIN;

@Component
@Slf4j
public class CadenceWorkerStarter {
    private static final Class<?>[] WORKFLOW_IMPLEMENTATION_TYPES = new Class[] { GreetingWorkflowImpl.class };
    private static final Object[] ACTIVITY_IMPLEMENTATIONS = new Object[] { new GreetingActivitiesImpl(System.out) };

    private final IWorkflowService workflowService;
    private final WorkerFactory workerFactory;
    private final WorkerOptions workerOptions;

    public CadenceWorkerStarter(IWorkflowService workflowService, WorkerFactory workerFactory, WorkerOptions workerOptions) {
        this.workflowService = workflowService;
        this.workerFactory = workerFactory;
        this.workerOptions = workerOptions;
    }

    @PostConstruct
    public void startWorkerFactory() throws TException {
        if (!domainExists()) {
            registerDomain();
        }

        createWorkers();

        log.info("Starting Cadence Worker Factory");
        workerFactory.start();
    }

    @PreDestroy
    public void shutdownWorkerFactory() {
        log.info("Shutdown Cadence Worker Factory");
        workerFactory.shutdown();
    }

    private void registerDomain() throws TException {
        RegisterDomainRequest request = new RegisterDomainRequest();
        request.setDescription(DOMAIN);
        request.setEmitMetric(false);
        request.setName(DOMAIN);
        request.setWorkflowExecutionRetentionPeriodInDays(2);

        workflowService.RegisterDomain(request);
        log.info("Successfully registered domain \"{}\"", DOMAIN);
    }

    private void createWorkers() {
        Worker worker = workerFactory.newWorker(GreetingWorkflow.TASK_LIST, workerOptions);

        worker.registerWorkflowImplementationTypes(WORKFLOW_IMPLEMENTATION_TYPES);
        worker.registerActivitiesImplementations(ACTIVITY_IMPLEMENTATIONS);
    }

    private boolean domainExists() throws TException {
        ListDomainsRequest listDomainsRequest = new ListDomainsRequest();
        ListDomainsResponse response = workflowService.ListDomains(listDomainsRequest);
        List<DescribeDomainResponse> domains = response.getDomains();

        return domains.stream()
                .anyMatch(d -> d.domainInfo.name.equals(DOMAIN));
    }
}
