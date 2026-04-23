package com.example.wf_orchestrator.workflow.dto;

import java.time.Instant;
  import java.util.Map;

  public record ExecutionEvent(
      String executionId,
      String stepId,
      String status,           // "SUCCESS" | "FAILED"
      Map<String, Object> output,
      String errorMessage,     // null if success
      Instant timestamp
  ) {

      public static Builder builder() {
          return new Builder();
      }

      public static class Builder {
          private String executionId;
          private String stepId;
          private String status;
          private Map<String, Object> output;
          private String errorMessage;
          private Instant timestamp;

          public Builder executionId(String executionId) {
              this.executionId = executionId;
              return this;
          }

          public Builder stepId(String stepId) {
              this.stepId = stepId;
              return this;
          }

          public Builder status(String status) {
              this.status = status;
              return this;
          }

          public Builder output(Map<String, Object> output) {
              this.output = output;
              return this;
          }

          public Builder errorMessage(String errorMessage) {
              this.errorMessage = errorMessage;
              return this;
          }

          public Builder timestamp(Instant timestamp) {
              this.timestamp = timestamp;
              return this;
          }

          public ExecutionEvent build() {
              return new ExecutionEvent(
                  executionId, stepId, status, output, errorMessage, timestamp
              );
          }
      }
  }
