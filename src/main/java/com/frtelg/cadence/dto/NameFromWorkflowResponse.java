package com.frtelg.cadence.dto;

import lombok.Value;

@Value
public class NameFromWorkflowResponse {
    String name;
    String workflowId;
}
