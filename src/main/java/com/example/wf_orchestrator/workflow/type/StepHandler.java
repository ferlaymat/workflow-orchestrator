package com.example.wf_orchestrator.workflow.type;

import com.example.wf_orchestrator.workflow.model.ExecutionContext;
import com.example.wf_orchestrator.workflow.model.StepResult;
import com.example.wf_orchestrator.workflow.model.WorkflowStep;
import reactor.core.publisher.Mono;

public interface StepHandler {
    StepType getType();
    Mono<StepResult> execute(WorkflowStep step,
                             ExecutionContext context);
}