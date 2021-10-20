package com.frtelg.cadence.dto;

import lombok.Value;

import java.util.Optional;

@Value
public class WorkflowResponse {
    String workflowId;
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    Optional<String> errorMessage;

    public static WorkflowResponse success(String workflowId) {
        return new WorkflowResponse(workflowId, Optional.empty());
    }

    public static WorkflowResponse error(String workflowId, String errorMessage) {
        return new WorkflowResponse(workflowId, Optional.of(errorMessage));
    }
}