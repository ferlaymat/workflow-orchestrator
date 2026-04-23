package com.example.wf_orchestrator.workflow.handler;

import com.example.wf_orchestrator.workflow.type.StepHandler;
import com.example.wf_orchestrator.workflow.type.StepType;
import com.example.wf_orchestrator.workflow.model.ExecutionContext;
import com.example.wf_orchestrator.workflow.model.StepResult;
import com.example.wf_orchestrator.workflow.model.WorkflowStep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@Slf4j
public class LogStepHandler implements StepHandler {

    @Override
    public StepType getType() {
        return StepType.LOG;
    }


    @Override
    public Mono<StepResult> execute(WorkflowStep step, ExecutionContext context) {
        // Extract message
        String message = (String) step.config().get("message");

        // Log it
        log.info("{}", message);

        // Return message
        return Mono.just(StepResult.ok(Map.of("logged", message)));
    }
}
