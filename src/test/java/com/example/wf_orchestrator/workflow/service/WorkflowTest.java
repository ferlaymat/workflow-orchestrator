package com.example.wf_orchestrator.workflow.service;

import com.example.wf_orchestrator.workflow.entity.Workflow;
import com.example.wf_orchestrator.workflow.type.WorkflowStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class WorkflowTest {

    private Workflow workflow;

    @BeforeEach
    void setUp() {
        workflow = Workflow.builder()
                .id("wf-001")
                .name("User Validation Workflow")
                .naturalLanguageDescription("A workflow to validate user data")
                .graphJson("{\"steps\": []}")
                .status(WorkflowStatus.DRAFT)
                .createdAt(Instant.parse("2024-01-01T10:00:00Z"))
                .updatedAt(Instant.parse("2024-01-01T10:00:00Z"))
                .build();
    }

    @Test
    void shouldCreateWorkflowWithBuilder() {
        // Then
        assertThat(workflow)
                .as("The Workflow must not be null")
                .isNotNull();

        assertThat(workflow.getId())
                .as("The workflow ID must be 'wf-001'")
                .isEqualTo("wf-001");

        assertThat(workflow.getName())
                .as("The workflow name must be 'User Validation Workflow'")
                .isEqualTo("User Validation Workflow");
    }

    @Test
    void shouldGetNaturalLanguageDescription() {
        // When
        String description = workflow.getNaturalLanguageDescription();

        // Then
        assertThat(description)
                .as("The natural language description must not be empty")
                .isNotEmpty();

        assertThat(description)
                .as("The description must be 'A workflow to validate user data'")
                .isEqualTo("A workflow to validate user data");

        System.out.println("Natural language description: " + description);
    }

    @Test
    void shouldGetGraphJson() {
        // When
        String graphJson = workflow.getGraphJson();

        // Then
        assertThat(graphJson)
                .as("The graph JSON must not be null")
                .isNotNull();

        assertThat(graphJson)
                .as("The graph JSON must contain valid JSON")
                .contains("steps");

        System.out.println("Graph JSON: " + graphJson);
    }

    @Test
    void shouldGetStatus() {
        // When
        WorkflowStatus status = workflow.getStatus();

        // Then
        assertThat(status)
                .as("The status must be DRAFT")
                .isEqualTo(WorkflowStatus.DRAFT);

        System.out.println("Workflow status: " + status);
    }

    @Test
    void shouldUpdateStatus() {
        // When
        workflow.setStatus(WorkflowStatus.ACTIVE);

        // Then
        assertThat(workflow.getStatus())
                .as("The status must be updated to ACTIVE")
                .isEqualTo(WorkflowStatus.ACTIVE);

        System.out.println("Updated workflow status: " + workflow.getStatus());
    }

    @Test
    void shouldHandleAllStatusTransitions() {
        // Given - DRAFT
        assertThat(workflow.getStatus())
                .as("Initial status must be DRAFT")
                .isEqualTo(WorkflowStatus.DRAFT);

        // When - Transition to ACTIVE
        workflow.setStatus(WorkflowStatus.ACTIVE);

        // Then
        assertThat(workflow.getStatus())
                .as("Status must be ACTIVE after transition")
                .isEqualTo(WorkflowStatus.ACTIVE);

        // When - Transition to ARCHIVED
        workflow.setStatus(WorkflowStatus.ARCHIVED);

        // Then
        assertThat(workflow.getStatus())
                .as("Status must be ARCHIVED after transition")
                .isEqualTo(WorkflowStatus.ARCHIVED);

        System.out.println("Status transition completed: DRAFT -> ACTIVE -> ARCHIVED");
    }

    @Test
    void shouldGetTimestamps() {
        // When
        Instant createdAt = workflow.getCreatedAt();
        Instant updatedAt = workflow.getUpdatedAt();

        // Then
        assertThat(createdAt)
                .as("The created date must not be null")
                .isNotNull();

        assertThat(updatedAt)
                .as("The updated date must not be null")
                .isNotNull();

        assertThat(createdAt)
                .as("Created date must be 2024-01-01T10:00:00Z")
                .isEqualTo(Instant.parse("2024-01-01T10:00:00Z"));

        assertThat(updatedAt)
                .as("Updated date must be 2024-01-01T10:00:00Z")
                .isEqualTo(Instant.parse("2024-01-01T10:00:00Z"));

        System.out.println("Created at: " + createdAt);
        System.out.println("Updated at: " + updatedAt);
    }

    @Test
    void shouldUpdateTimestamp() {
        // Given
        Instant newTimestamp = Instant.parse("2024-01-02T12:00:00Z");

        // When
        workflow.setUpdatedAt(newTimestamp);

        // Then
        assertThat(workflow.getUpdatedAt())
                .as("The updated date must be changed")
                .isEqualTo(newTimestamp);

        System.out.println("Updated timestamp changed to: " + workflow.getUpdatedAt());
    }

    @Test
    void shouldCreateWorkflowWithNoArgsConstructor() {
        // When
        Workflow emptyWorkflow = new Workflow();

        // Then
        assertThat(emptyWorkflow)
                .as("The Workflow must be created with no-args constructor")
                .isNotNull();

        System.out.println("Empty workflow created: " + emptyWorkflow);
    }

    @Test
    void shouldSetAllFields() {
        // Given
        Workflow testWorkflow = new Workflow();

        // When
        testWorkflow.setId("wf-002");
        testWorkflow.setName("Test Workflow");
        testWorkflow.setNaturalLanguageDescription("Test description");
        testWorkflow.setGraphJson("{\"test\": true}");
        testWorkflow.setStatus(WorkflowStatus.ACTIVE);
        testWorkflow.setCreatedAt(Instant.now());
        testWorkflow.setUpdatedAt(Instant.now());

        // Then
        assertThat(testWorkflow.getId())
                .as("The ID must be set")
                .isEqualTo("wf-002");

        assertThat(testWorkflow.getName())
                .as("The name must be set")
                .isEqualTo("Test Workflow");

        assertThat(testWorkflow.getStatus())
                .as("The status must be set to ACTIVE")
                .isEqualTo(WorkflowStatus.ACTIVE);

        System.out.println("All fields set successfully: " + testWorkflow);
    }

    @Test
    void shouldGenerateToString() {
        // When
        String toString = workflow.toString();

        // Then
        assertThat(toString)
                .as("The toString must not be empty")
                .isNotEmpty();

        assertThat(toString)
                .as("The toString must contain the workflow name")
                .contains("User Validation Workflow");

        assertThat(toString)
                .as("The toString must contain the status")
                .contains("DRAFT");

        System.out.println("Workflow toString: " + toString);
    }
}
