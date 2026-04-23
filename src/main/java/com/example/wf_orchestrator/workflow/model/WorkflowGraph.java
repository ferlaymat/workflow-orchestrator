package com.example.wf_orchestrator.workflow.model;

import java.util.List;

public record WorkflowGraph(
    List<WorkflowStep> steps,
    List<WorkflowEdge> edges
) {}