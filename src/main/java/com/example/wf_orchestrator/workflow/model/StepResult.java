package com.example.wf_orchestrator.workflow.model;

import jakarta.annotation.Nullable;

import java.util.Map;

public record StepResult(
    boolean success,
    Map<String, Object> output,
    @Nullable String errorMessage
) {
    public static StepResult ok(Map<String, Object> output) {
        return new StepResult(true, output, null);
    }

    public static StepResult fail(String error) {
        return new StepResult(false, Map.of(), error);
    }
}