package com.example.wf_orchestrator.workflow.dto;

import jakarta.validation.constraints.NotBlank;

  public record WorkflowRequest(
   
      @NotBlank(message = "Name is required")
      String name,
   
      @NotBlank(message = "Description is required")
      String description
  ) {}
