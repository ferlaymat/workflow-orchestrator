package com.example.wf_orchestrator.workflow.dto;

import com.example.wf_orchestrator.workflow.type.WorkflowStatus;
import com.example.wf_orchestrator.workflow.entity.Workflow;
import com.example.wf_orchestrator.workflow.model.ExecutionSummary;
import com.example.wf_orchestrator.workflow.model.WorkflowGraph;

import java.time.Instant;
  import java.util.List;

  public record WorkflowResponse(
          String id,
          String name,
          String naturalLanguageDescription,
          WorkflowGraph graph,
          WorkflowStatus status,
          Instant createdAt,
          Instant updatedAt,
          List<ExecutionSummary> executions
  ) {
      // Méthode factory depuis l'entité
      public static WorkflowResponse from(Workflow workflow) {
          return new WorkflowResponse(
              workflow.getId(),
              workflow.getName(),
              workflow.getNaturalLanguageDescription(),
              null,
              workflow.getStatus(),
              workflow.getCreatedAt(),
              workflow.getUpdatedAt(),
              List.of()
          );
      }

      // Surcharge quand on a le graphe désérialisé
      public static WorkflowResponse from(Workflow workflow, WorkflowGraph graph) {
          return new WorkflowResponse(
              workflow.getId(),
              workflow.getName(),
              workflow.getNaturalLanguageDescription(),
              graph,
              workflow.getStatus(),
              workflow.getCreatedAt(),
              workflow.getUpdatedAt(),
              List.of()
          );
      }
  }
