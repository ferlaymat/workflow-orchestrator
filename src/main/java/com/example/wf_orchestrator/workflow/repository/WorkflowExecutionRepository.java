package com.example.wf_orchestrator.workflow.repository;

import com.example.wf_orchestrator.workflow.type.ExecutionStatus;
import com.example.wf_orchestrator.workflow.entity.WorkflowExecution;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface WorkflowExecutionRepository
        extends ReactiveCrudRepository<WorkflowExecution, String> {

    Flux<WorkflowExecution> findByWorkflowId(String workflowId);

    Flux<WorkflowExecution> findByWorkflowIdAndStatus(
        String workflowId, ExecutionStatus status);
}