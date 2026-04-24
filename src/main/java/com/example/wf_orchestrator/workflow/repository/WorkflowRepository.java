package com.example.wf_orchestrator.workflow.repository;

import com.example.wf_orchestrator.workflow.type.WorkflowStatus;
import com.example.wf_orchestrator.workflow.entity.Workflow;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface WorkflowRepository extends ReactiveCrudRepository<Workflow, String> {

    Flux<Workflow> findByStatus(WorkflowStatus status);

    @Query("SELECT * FROM workflows ORDER BY created_at DESC LIMIT :limit")
    Flux<Workflow> findRecent(@Param("limit") int limit);

    Flux<Workflow> findAllBy(Pageable pageable);
}