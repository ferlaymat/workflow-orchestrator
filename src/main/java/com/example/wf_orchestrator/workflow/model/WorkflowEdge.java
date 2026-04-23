package com.example.wf_orchestrator.workflow.model;

import jakarta.annotation.Nullable;

public record WorkflowEdge(
    String from,
    String to,
    @Nullable String condition
) {}