package com.example.wf_orchestrator.workflow.handler;

import com.example.wf_orchestrator.workflow.type.StepHandler;
import com.example.wf_orchestrator.workflow.type.StepType;
import com.example.wf_orchestrator.workflow.model.ExecutionContext;
import com.example.wf_orchestrator.workflow.model.StepResult;
import com.example.wf_orchestrator.workflow.model.WorkflowStep;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class HttpStepHandler implements StepHandler {

    private final WebClient webClient;

    @Override
    public StepType getType() {
        return StepType.HTTP_CALL;
    }

    @Override
    public Mono<StepResult> execute(WorkflowStep step, ExecutionContext context) {
        // 1. Extract url, method, headers
        String url = (String) step.config().get("url");
        String method = (String) step.config().get("method"); // GET, POST...
        Map<String, String> headerMap = (Map<String, String>) step.config().get("headers");

        // Reactive HTTP call
        return webClient
                .method(HttpMethod.valueOf(method))
                .uri(url)
                .headers(h -> {
                    if (headerMap != null) {
                        headerMap.forEach(h::set);
                    }
                })
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> StepResult.ok(Map.of("response", response)))  // success
                .onErrorResume(e -> Mono.just(StepResult.fail(e.getMessage())));    // failed
    }
}
