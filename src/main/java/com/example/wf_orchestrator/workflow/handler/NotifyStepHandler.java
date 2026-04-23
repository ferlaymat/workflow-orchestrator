package com.example.wf_orchestrator.workflow.handler;

import com.example.wf_orchestrator.workflow.type.StepHandler;
import com.example.wf_orchestrator.workflow.type.StepType;
import com.example.wf_orchestrator.workflow.model.ExecutionContext;
import com.example.wf_orchestrator.workflow.model.StepResult;
import com.example.wf_orchestrator.workflow.model.WorkflowStep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@Slf4j
public class NotifyStepHandler implements StepHandler {

    private final WebClient webClient = WebClient.create();

    @Override
    public StepType getType() {
        return StepType.NOTIFY;
    }

    @Override
    public Mono<StepResult> execute(WorkflowStep step, ExecutionContext context) {
        // Extract config
        String channel = getConfig(step, "channel", "webhook");
        String url = getConfig(step, "url");
        String body = getConfig(step, "body");

        if (body == null || body.isBlank()) {
            return Mono.just(StepResult.fail("body is required for NOTIFY"));
        }

        // Dispatch on right channel
        return switch (channel) {
            case "webhook", "slack" -> sendWebhook(url, body);
            default -> Mono.just(StepResult.fail("Unknown channel: " + channel + ". Available:  webhook, slack"));
        };
    }


    private Mono<StepResult> sendWebhook(String url, String body) {
        if (url == null || url.isBlank()) {
            return Mono.just(StepResult.fail("url is required for webhook channel"));
        }

        return webClient
            .post()
            .uri(url)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .retrieve()
            .bodyToMono(String.class)
            .map(response -> StepResult.ok(Map.of(
                "channel", "webhook",
                "response", response
            )))
            .onErrorResume(e -> {
                log.error("Webhook error: {}", e.getMessage());
                return Mono.just(StepResult.fail("Webhook error: " + e.getMessage()));
            });
    }

    @SuppressWarnings("unchecked")
    private <T> T getConfig(WorkflowStep step, String key, T defaultValue) {
        Object value = step.config().get(key);
        return value != null ? (T) value : defaultValue;
    }

    private <T> T getConfig(WorkflowStep step, String key) {
        return (T) step.config().get(key);
    }
}
