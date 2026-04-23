package com.example.wf_orchestrator.workflow.engine;

import com.example.wf_orchestrator.workflow.dto.ExecutionEvent;
import com.example.wf_orchestrator.workflow.entity.Workflow;
import com.example.wf_orchestrator.workflow.entity.WorkflowExecution;
import com.example.wf_orchestrator.workflow.model.ExecutionContext;
import com.example.wf_orchestrator.workflow.model.WorkflowEdge;
import com.example.wf_orchestrator.workflow.model.WorkflowGraph;
import com.example.wf_orchestrator.workflow.model.WorkflowStep;
import com.example.wf_orchestrator.workflow.repository.WorkflowExecutionRepository;
import com.example.wf_orchestrator.workflow.type.ExecutionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class WorkflowEngine {

    private final StepRegistry registry;
    private final WorkflowExecutionRepository executionRepo;
    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Flux<ExecutionEvent> execute(Workflow workflow, Map<String, Object> inputs) {
        var execution = createExecution(workflow);
        var graph = parseGraph(workflow.getGraphJson());

        return Flux.fromIterable(topologicalSort(graph.steps(), graph.edges()))
                .concatMap(step -> executeStep(step, execution, inputs))
                .doOnComplete(() -> finalizeExecution(execution, ExecutionStatus.SUCCESS))
                .doOnError(e -> finalizeExecution(execution, ExecutionStatus.FAILED));
    }

    private Mono<ExecutionEvent> executeStep(WorkflowStep step,
                                             WorkflowExecution execution,
                                             Map<String, Object> inputs) {
        log.info("Executing step {} ({})", step.id(), step.type());
        var handler = registry.get(step.type());
        var context = new ExecutionContext(execution.getId(), inputs);

        return handler.execute(step, context)
                .map(result -> ExecutionEvent.builder()
                        .executionId(execution.getId())
                        .stepId(step.id())
                        .status(result.success() ? "SUCCESS" : "FAILED")
                        .output(result.output())
                        .timestamp(Instant.now())
                        .build())
                .doOnNext(event -> persistStepLog(execution, event));
    }

    /**
     * Generate unique id for the execution
     * link the execution to the parent workflow by it id
     * initialize to RUNNING
     * create empty JSON
     */
    private WorkflowExecution createExecution(Workflow workflow) {
        return WorkflowExecution.builder()
                .id(UUID.randomUUID().toString())
                .workflowId(workflow.getId())
                .status(ExecutionStatus.RUNNING)
                .stepsLog("{}")
                .startedAt(Instant.now())
                .build();
    }

    //deserialize stored JSON into workflow graph
    private WorkflowGraph parseGraph(String graphJson) {
        return objectMapper.readValue(graphJson, WorkflowGraph.class);
    }

    private List<WorkflowStep> topologicalSort(List<WorkflowStep> steps,
                                               List<WorkflowEdge> edges) {
        // Compute all step's links
        Map<String, Integer> inDegree = new HashMap<>();
        Map<String, WorkflowStep> stepMap = new HashMap<>();

        /*
        *   Example with this graph :
        *   Step1 → Step2 → Step3
        *     ↓       ↓
        *   Step4 → Step5
        *
        *   inDegree = {
        *     "step1": 0,    // no one point to step1
        *     "step2": 1,    // step1 point to step2
        *     "step3": 1,    // step2 point to step3
        *     "step4": 1,    // step1 point to step4
        *     "step5": 2     // step2 and step4 point to step5
        *   }
        * */
        for (WorkflowStep step : steps) {
            //initialize all indegree to 0
            inDegree.put(step.id(), 0);
            //initial map step by id
            stepMap.put(step.id(), step);
        }

        for (WorkflowEdge edge : edges) {
            //foreach indegree we sum all links to it
            inDegree.merge(edge.to(), 1, Integer::sum);
        }

        // set entry steps in a queue
        Queue<String> queue = new LinkedList<>();
        for (Map.Entry<String, Integer> entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) {
                //add all steps without link.should be the first step
                queue.add(entry.getKey());
            }
        }

        // sort
        List<WorkflowStep> result = new ArrayList<>();
        while (!queue.isEmpty()) {
            String currentId = queue.poll();
            result.add(stepMap.get(currentId));

            // find the next step to add in the queue and decrement link
            for (WorkflowEdge edge : edges) {
                if (edge.from().equals(currentId)) {
                    int newDegree = inDegree.merge(edge.to(), -1, Integer::sum);
                    if (newDegree == 0) {
                        queue.add(edge.to());
                    }
                }
            }
        }

        // Detect infinite loop
        if (result.size() != steps.size()) {
            throw new IllegalStateException("Cycle detected in workflow graph");
        }

        return result;
    }

    //store in db the final status(success or failed)
    private void finalizeExecution(WorkflowExecution execution, ExecutionStatus status) {
        execution.setStatus(status);
        execution.setFinishedAt(Instant.now());

        executionRepo.save(execution).subscribe();
    }


    private void persistStepLog(WorkflowExecution execution, ExecutionEvent event) {
        // read log from previous steps
        List<Map<String, Object>> logs = new ArrayList<>();
        String currentLogs = execution.getStepsLog();
        if (currentLogs != null && !currentLogs.isBlank()) {
            logs = objectMapper.readValue(currentLogs,
                    new TypeReference<>() {
                    });
        }

        // add the new event
        logs.add(Map.of(
                "stepId", event.stepId(),
                "status", event.status(),
                "output", event.output(),
                "timestamp", event.timestamp().toString()
        ));

        // update the execution object with updated log
        execution.setStepsLog(objectMapper.writeValueAsString(logs));

    }

}