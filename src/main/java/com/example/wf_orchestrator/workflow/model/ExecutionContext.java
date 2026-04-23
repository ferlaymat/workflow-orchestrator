package com.example.wf_orchestrator.workflow.model;

import java.util.Map;


//execution id, input data of the workflow, data forwarded between steps
public record ExecutionContext(
    String executionId,
    Map<String, Object> inputs,
    Map<String, Object> variables
) {
    public ExecutionContext(String executionId, Map<String, Object> inputs) {
        this(executionId, inputs, Map.of());
    }

    public Object getInput(String key) {
        return inputs.get(key);
    }


    public Object getVariable(String key) {
        return variables.get(key);
    }

    public ExecutionContext withVariable(String key, Object value) {
        var newVars = Map.copyOf(variables);
        return new ExecutionContext(executionId, inputs, newVars);
    }
}
