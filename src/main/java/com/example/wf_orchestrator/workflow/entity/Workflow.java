package com.example.wf_orchestrator.workflow.entity;


import com.example.wf_orchestrator.workflow.type.WorkflowStatus;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("workflows")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Workflow {

    @Id
    private String id;

    private String name;
    private String naturalLanguageDescription;
    private String graphJson;

    private WorkflowStatus status;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;


}