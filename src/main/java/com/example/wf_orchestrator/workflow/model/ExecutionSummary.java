package com.example.wf_orchestrator.workflow.model;

import com.example.wf_orchestrator.workflow.type.ExecutionStatus;

import java.time.Instant;

public record ExecutionSummary(
      String id,
      ExecutionStatus status,
      Instant startedAt,
      Instant finishedAt
  ) {}
