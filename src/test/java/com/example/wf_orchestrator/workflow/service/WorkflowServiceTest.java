package com.example.wf_orchestrator.workflow.service;

import com.example.wf_orchestrator.workflow.dto.WorkflowRequest;
import com.example.wf_orchestrator.workflow.dto.WorkflowResponse;
import com.example.wf_orchestrator.workflow.dto.WorkflowWithExecutions;
import com.example.wf_orchestrator.workflow.entity.Workflow;
import com.example.wf_orchestrator.workflow.entity.WorkflowExecution;
import com.example.wf_orchestrator.workflow.model.WorkflowGraph;
import com.example.wf_orchestrator.workflow.repository.WorkflowExecutionRepository;
import com.example.wf_orchestrator.workflow.repository.WorkflowRepository;
import com.example.wf_orchestrator.workflow.type.ExecutionStatus;
import com.example.wf_orchestrator.workflow.type.WorkflowStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkflowServiceTest {

    @Mock
    private WorkflowRepository workflowRepo;

    @Mock
    private WorkflowExecutionRepository executionRepo;

    @Mock
    private WorkflowParserService parserService;

    private WorkflowService workflowService;

    @BeforeEach
    void setUp() {
        workflowService = new WorkflowService(workflowRepo, executionRepo, parserService);
    }

    @Test
    void shouldCreateWorkflowFromNaturalLanguage() {
        // Given
        WorkflowRequest request = new WorkflowRequest("User Validation", "Validate user email and send confirmation");
        WorkflowGraph mockGraph = new WorkflowGraph(List.of(), List.of());

        Workflow savedWorkflow = Workflow.builder()
                .id("wf-001")
                .name("User Validation")
                .naturalLanguageDescription("Validate user email and send confirmation")
                .graphJson("{\"steps\":[],\"edges\":[]}")
                .status(WorkflowStatus.DRAFT)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        when(parserService.parse(request.description())).thenReturn(Mono.just(mockGraph));
        when(workflowRepo.save(any(Workflow.class))).thenReturn(Mono.just(savedWorkflow));

        // When
        Mono<WorkflowResponse> result = workflowService.createFromNaturalLanguage(request);

        // Then
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertThat(response)
                            .as("The WorkflowResponse must not be null")
                            .isNotNull();

                    assertThat(response.id())
                            .as("The ID must be 'wf-001'")
                            .isEqualTo("wf-001");

                    assertThat(response.name())
                            .as("The name must be 'User Validation'")
                            .isEqualTo("User Validation");

                    assertThat(response.status())
                            .as("The status must be DRAFT")
                            .isEqualTo(WorkflowStatus.DRAFT);
                })
                .verifyComplete();

        verify(parserService).parse(request.description());
        verify(workflowRepo).save(any(Workflow.class));

        System.out.println("Workflow created successfully: " + savedWorkflow.getId());
    }

    @Test
    void shouldFindAllWorkflows() {
        // Given
        Workflow workflow1 = Workflow.builder()
                .id("wf-001")
                .name("Workflow 1")
                .naturalLanguageDescription("First workflow")
                .status(WorkflowStatus.DRAFT)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        Workflow workflow2 = Workflow.builder()
                .id("wf-002")
                .name("Workflow 2")
                .naturalLanguageDescription("Second workflow")
                .status(WorkflowStatus.ACTIVE)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        when(workflowRepo.findAllBy(any())).thenReturn(Flux.just(workflow1, workflow2));
        when(workflowRepo.count()).thenReturn(Mono.just(2L));

        // When
        Mono<Page<WorkflowResponse>> result = workflowService.findAll(0, 10, "name", "asc");

        // Then
        StepVerifier.create(result)
                .assertNext(page -> {
                    assertThat(page)
                            .as("The page must not be null")
                            .isNotNull();

                    assertThat(page.getContent())
                            .as("The page must contain 2 workflows")
                            .hasSize(2);

                    assertThat(page.getContent().get(0).id())
                            .as("First workflow ID must be 'wf-001'")
                            .isEqualTo("wf-001");

                    assertThat(page.getContent().get(0).name())
                            .as("First workflow name must be 'Workflow 1'")
                            .isEqualTo("Workflow 1");

                    assertThat(page.getContent().get(1).id())
                            .as("Second workflow ID must be 'wf-002'")
                            .isEqualTo("wf-002");

                    assertThat(page.getContent().get(1).name())
                            .as("Second workflow name must be 'Workflow 2'")
                            .isEqualTo("Workflow 2");

                    assertThat(page.getTotalElements())
                            .as("Total elements must be 2")
                            .isEqualTo(2L);
                })
                .verifyComplete();

        verify(workflowRepo).findAllBy(any());
        verify(workflowRepo).count();

        System.out.println("Found 2 workflows");
    }

    @Test
    void shouldFindWorkflowById() {
        // Given
        String workflowId = "wf-001";
        Workflow workflow = Workflow.builder()
                .id(workflowId)
                .name("Test Workflow")
                .naturalLanguageDescription("Test description")
                .status(WorkflowStatus.ACTIVE)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        when(workflowRepo.findById(workflowId)).thenReturn(Mono.just(workflow));

        // When
        Mono<WorkflowResponse> result = workflowService.findById(workflowId);

        // Then
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertThat(response)
                            .as("The WorkflowResponse must not be null")
                            .isNotNull();

                    assertThat(response.id())
                            .as("The ID must be 'wf-001'")
                            .isEqualTo(workflowId);

                    assertThat(response.name())
                            .as("The name must be 'Test Workflow'")
                            .isEqualTo("Test Workflow");
                })
                .verifyComplete();

        verify(workflowRepo).findById(workflowId);

        System.out.println("Workflow found by ID: " + workflowId);
    }

    @Test
    void shouldThrowExceptionWhenWorkflowNotFound() {
        // Given
        String nonExistentId = "wf-999";
        when(workflowRepo.findById(nonExistentId)).thenReturn(Mono.empty());

        // When
        Mono<WorkflowResponse> result = workflowService.findById(nonExistentId);

        // Then
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalStateException &&
                                throwable.getMessage().equals(nonExistentId))
                .verify();

        System.out.println("Exception thrown for non-existent workflow: " + nonExistentId);
    }

    @Test
    void shouldFindWorkflowWithExecutions() {
        // Given
        String workflowId = "wf-001";
        Workflow workflow = Workflow.builder()
                .id(workflowId)
                .name("Workflow with Executions")
                .naturalLanguageDescription("Test")
                .status(WorkflowStatus.ACTIVE)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        WorkflowExecution execution1 = WorkflowExecution.builder()
                .id("exec-001")
                .workflowId(workflowId)
                .status(ExecutionStatus.RUNNING)
                .build();

        WorkflowExecution execution2 = WorkflowExecution.builder()
                .id("exec-002")
                .workflowId(workflowId)
                .status(ExecutionStatus.SUCCESS)
                .build();

        when(workflowRepo.findById(workflowId)).thenReturn(Mono.just(workflow));
        when(executionRepo.findByWorkflowId(workflowId)).thenReturn(Flux.just(execution1, execution2));

        // When
        Mono<WorkflowWithExecutions> result = workflowService.findWithExecutions(workflowId);

        // Then
        StepVerifier.create(result)
                .assertNext(withExecutions -> {
                    assertThat(withExecutions)
                            .as("The WorkflowWithExecutions must not be null")
                            .isNotNull();

                    assertThat(withExecutions.workflow())
                            .as("The workflow must not be null")
                            .isNotNull();

                    assertThat(withExecutions.workflow().getId())
                            .as("The workflow ID must be 'wf-001'")
                            .isEqualTo(workflowId);

                    assertThat(withExecutions.executions())
                            .as("The executions list must contain 2 executions")
                            .hasSize(2);
                })
                .verifyComplete();

        System.out.println("Workflow with executions found: " + workflowId);
    }

    @Test
    void shouldDeleteWorkflow() {
        // Given
        String workflowId = "wf-001";
        when(workflowRepo.deleteById(workflowId)).thenReturn(Mono.empty());

        // When
        Mono<Void> result = workflowService.delete(workflowId);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(workflowRepo).deleteById(workflowId);

        System.out.println("Workflow deleted: " + workflowId);
    }

    @Test
    void shouldHandleEmptyWorkflowList() {
        // Given
        when(workflowRepo.findAllBy(any())).thenReturn(Flux.empty());
        when(workflowRepo.count()).thenReturn(Mono.just(0L));

        // When
        Mono<Page<WorkflowResponse>> result = workflowService.findAll(0, 10, "name", "asc");

        // Then
        StepVerifier.create(result)
                .assertNext(page -> {
                    assertThat(page)
                            .as("The page must not be null")
                            .isNotNull();

                    assertThat(page.getContent())
                            .as("The page must be empty")
                            .isEmpty();

                    assertThat(page.getTotalElements())
                            .as("Total elements must be 0")
                            .isEqualTo(0L);
                })
                .verifyComplete();

        verify(workflowRepo).findAllBy(any());
        verify(workflowRepo).count();

        System.out.println("No workflows found - empty list handled correctly");
    }

    @Test
    void shouldHandleWorkflowWithNoExecutions() {
        // Given
        String workflowId = "wf-001";
        Workflow workflow = Workflow.builder()
                .id(workflowId)
                .name("Workflow without Executions")
                .naturalLanguageDescription("Test")
                .status(WorkflowStatus.DRAFT)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        when(workflowRepo.findById(workflowId)).thenReturn(Mono.just(workflow));
        when(executionRepo.findByWorkflowId(workflowId)).thenReturn(Flux.empty());

        // When
        Mono<WorkflowWithExecutions> result = workflowService.findWithExecutions(workflowId);

        // Then
        StepVerifier.create(result)
                .assertNext(withExecutions -> {
                    assertThat(withExecutions.executions())
                            .as("The executions list must be empty")
                            .isEmpty();
                })
                .verifyComplete();

        System.out.println("Workflow with no executions found: " + workflowId);
    }
}
