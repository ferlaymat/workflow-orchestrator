package com.example.wf_orchestrator.workflow.dto;

import com.example.wf_orchestrator.workflow.entity.Workflow;
import com.example.wf_orchestrator.workflow.entity.WorkflowExecution;

import java.util.List;

public record WorkflowWithExecutions(
        Workflow workflow,
        List<WorkflowExecution> executions
) {
}

