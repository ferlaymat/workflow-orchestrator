package com.example.wf_orchestrator.workflow.model;

import com.example.wf_orchestrator.workflow.type.StepType;

import java.util.Map;

public record WorkflowStep(
    String id,
    StepType type,
    String label,
    Map<String, Object> config
) {}