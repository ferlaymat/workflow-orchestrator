package com.example.wf_orchestrator.workflow.engine;

import com.example.wf_orchestrator.workflow.type.StepHandler;
import com.example.wf_orchestrator.workflow.type.StepType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class StepRegistry {

    private final Map<StepType, StepHandler> handlers;

    public StepRegistry(List<StepHandler> handlerList) {
        this.handlers = handlerList.stream()
            .collect(Collectors.toMap(StepHandler::getType, h -> h));
    }

    public StepHandler get(StepType type) {
        return Optional.ofNullable(handlers.get(type))
            .orElseThrow(() -> new IllegalStateException("Unknown step type: " + type));
    }
}