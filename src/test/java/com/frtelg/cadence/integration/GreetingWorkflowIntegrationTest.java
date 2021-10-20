package com.frtelg.cadence.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.frtelg.cadence.configuration.EnableCadenceIntegrationTest;
import com.frtelg.cadence.dto.NameFromWorkflowResponse;
import com.frtelg.cadence.dto.WorkflowResponse;
import com.uber.cadence.EventType;
import com.uber.cadence.GetWorkflowExecutionHistoryRequest;
import com.uber.cadence.HistoryEvent;
import com.uber.cadence.WorkflowExecution;
import com.uber.cadence.client.WorkflowClient;
import org.apache.thrift.TException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@EnableCadenceIntegrationTest
class GreetingWorkflowIntegrationTest {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final WorkflowClient workflowClient;

    @Autowired
    public GreetingWorkflowIntegrationTest(WebApplicationContext webApplicationContext, ObjectMapper objectMapper, WorkflowClient workflowClient) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        this.objectMapper = objectMapper;
        this.workflowClient = workflowClient;
    }

    @Test
    void integrationTest() throws Exception {
        // Start workflow
        var startWorkflowRequest = MockMvcRequestBuilders.get("/workflow")
                .accept(MediaType.APPLICATION_JSON);

        var startWorkflowResponse = mockMvc.perform(startWorkflowRequest)
                .andExpect(status().isOk())
                .andReturn();

        var startWorkflowResponseBodyJson = startWorkflowResponse.getResponse()
                .getContentAsString();

        var startWorkflowResponseBody = objectMapper.readValue(startWorkflowResponseBodyJson, WorkflowResponse.class);

        var workflowId = startWorkflowResponseBody.getWorkflowId();

        // Query workflow after start
        var queryWorkflowBeforeTriggerRequest = MockMvcRequestBuilders.get("/workflow/" + workflowId + "/current-name")
                .accept(MediaType.APPLICATION_JSON);

        var queryWorkflowBeforeTriggerResponse = mockMvc.perform(queryWorkflowBeforeTriggerRequest)
                .andExpect(status().isOk())
                .andReturn();

        var queryWorkflowBeforeTriggerResponseBodyJson = queryWorkflowBeforeTriggerResponse.getResponse()
                .getContentAsString();

        var queryWorkflowBeforeTriggerResponseBody = objectMapper.readValue(queryWorkflowBeforeTriggerResponseBodyJson, NameFromWorkflowResponse.class);

        assertEquals("Stranger", queryWorkflowBeforeTriggerResponseBody.getName());

        // Trigger workflow
        var triggerWorkflowRequest = MockMvcRequestBuilders.put("/workflow/" + workflowId + "/Telg")
                .accept(MediaType.APPLICATION_JSON);

        var triggerWorkflowResponse = mockMvc.perform(triggerWorkflowRequest)
                .andExpect(status().isOk())
                .andReturn();

        var triggerWorkflowResponseBodyJson = triggerWorkflowResponse.getResponse()
                .getContentAsString();

        var triggerWorkflowResponseBody = objectMapper.readValue(triggerWorkflowResponseBodyJson, WorkflowResponse.class);

        assertEquals(workflowId, triggerWorkflowResponseBody.getWorkflowId());

        // Query workflow after trigger
        var queryWorkflowAfterTriggerRequest = MockMvcRequestBuilders.get("/workflow/" + workflowId + "/current-name")
                .accept(MediaType.APPLICATION_JSON);

        var queryWorkflowAfterTriggerResponse = mockMvc.perform(queryWorkflowAfterTriggerRequest)
                .andExpect(status().isOk())
                .andReturn();

        var queryWorkflowAfterTriggerResponseBodyJson = queryWorkflowAfterTriggerResponse.getResponse()
                .getContentAsString();

        var queryWorkflowAfterTriggerResponseBody = objectMapper.readValue(queryWorkflowAfterTriggerResponseBodyJson, NameFromWorkflowResponse.class);

        assertEquals("Telg", queryWorkflowAfterTriggerResponseBody.getName());

        // Terminate workflow
        var terminateWorkflowRequest = MockMvcRequestBuilders.delete("/workflow/" + workflowId)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(terminateWorkflowRequest)
                .andExpect(status().isOk())
                .andReturn();

        await().atMost(1000, TimeUnit.MILLISECONDS)
                .until(() -> getLatestWorkflowEvent(workflowId).eventType == EventType.WorkflowExecutionCompleted);

        assertEquals(EventType.WorkflowExecutionCompleted, getLatestWorkflowEvent(workflowId).eventType);
    }

    private HistoryEvent getLatestWorkflowEvent(String workflowId) throws TException {
        var workflowHistory = workflowClient.getService().GetWorkflowExecutionHistory(
                new GetWorkflowExecutionHistoryRequest()
                        .setDomain(workflowClient.getOptions().getDomain())
                        .setExecution(new WorkflowExecution().setWorkflowId(workflowId)));

        return workflowHistory.history.events.get(workflowHistory.getRawHistorySize() - 1);
    }
}
