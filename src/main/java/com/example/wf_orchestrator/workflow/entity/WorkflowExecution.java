package com.example.wf_orchestrator.workflow.entity;


import com.example.wf_orchestrator.workflow.type.ExecutionStatus;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("workflow_executions")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowExecution {

    @Id
    private String id;

    private String workflowId;

    private ExecutionStatus status;

    private String stepsLog;
    private String errorMessage;

    @CreatedDate
    private Instant startedAt;

    private Instant finishedAt;
}