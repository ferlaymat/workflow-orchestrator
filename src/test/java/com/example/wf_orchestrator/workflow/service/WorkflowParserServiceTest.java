package com.example.wf_orchestrator.workflow.service;

import com.example.wf_orchestrator.workflow.model.WorkflowGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class WorkflowParserServiceTest {

    @Autowired
    private WorkflowParserService workflowParserService;

    @Autowired(required = false)
    private ChatClient chatClient;

    @BeforeEach
    void setUp() {
        // Ollama must be available
        assertThat(chatClient)
                .as("ChatClient must be configure. Check Ollama is well start with : 'ollama list' or 'ollama serve' to start")
                .isNotNull();
    }

    @Test
    void shouldParseSimpleWorkflow() {
        // Given
        String input = "Create a simple workflow with 2 steps: start and finish";

        // When
        WorkflowGraph result = workflowParserService.parse(input).block();

        // Then
        assertThat(result)
                .as("The WorkflowGraph must not be empty")
                .isNotNull();

        assertThat(result.steps())
                .as("It must contains steps")
                .isNotEmpty();

        System.out.println("Ollama answered");
        System.out.println("Generated steps: " + result.steps().size());
        result.steps().forEach(step -> System.out.println("  - " + step.id() + ": " + step.label()));

    }

    @Test
    void shouldParseWorkflowWithConditions() {
        // Given
        String input = "Create a workflow with: step A, step B, and condition A to B";

        // When
        WorkflowGraph result = workflowParserService.parse(input).block();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.edges())
                .as("The workflow must have connections")
                .isNotEmpty();

        System.out.println("Generated workflow");
        result.edges().forEach(edge -> System.out.println("  - " + edge.from() + " -> " + edge.to()));
    }

    @Test
    void shouldHandleComplexWorkflow() {
        // Given
        String input = """
                Create a user validating workflow with:
                - one step to received data
                - one step to validate email
                - If email is valid: one step to send confirmation
                - If email is not valid: one step to notify the error
                """;

        // When
        WorkflowGraph result = workflowParserService.parse(input).block();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.steps()).hasSizeGreaterThanOrEqualTo(3);

        System.out.println("Complex workflow generated with " + result.steps().size() + " steps");
        System.out.println(result);
    }
}
