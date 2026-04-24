package com.example.wf_orchestrator.workflow.controller;

import com.example.wf_orchestrator.workflow.dto.ExecutionEvent;
import com.example.wf_orchestrator.workflow.dto.WorkflowRequest;
import com.example.wf_orchestrator.workflow.dto.WorkflowResponse;
import com.example.wf_orchestrator.workflow.engine.WorkflowEngine;
import com.example.wf_orchestrator.workflow.service.WorkflowService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/workflow")
@RequiredArgsConstructor
public class WorkflowController {

    private final WorkflowService workflowService;
    private final WorkflowEngine workflowEngine;


    //screen 1 - list/delete/execute
    @GetMapping
    public Mono<Page<WorkflowResponse>> getAllWorkFlows(@RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size,
                                                        @RequestParam(defaultValue = "name") String sortBy,
                                                        @RequestParam(defaultValue = "asc") String sortOrder) {
        return workflowService.findAll( page,  size, sortBy,  sortOrder);
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        workflowService.delete(id);
    }


    @GetMapping(value = "/{id}/execute",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE)  // ← SSE
    public Flux<ServerSentEvent<ExecutionEvent>> execute(
            @PathVariable String id,
            @RequestParam(defaultValue = "{}") String inputs) {

        return workflowService.findById(id)
                .flatMapMany(workflow -> workflowEngine.execute(workflow, inputs))
                .map(event -> ServerSentEvent.<ExecutionEvent>builder()
                        .id(event.stepId())
                        .event("step-completed")
                        .data(event)
                        .build())
                .concatWith(Mono.just(ServerSentEvent.<ExecutionEvent>builder()
                        .event("execution-done")
                        .data(null)
                        .build()));
    }

    //screen 2 - create

    @PostMapping
    public Mono<WorkflowResponse> create(@Valid @RequestBody WorkflowRequest request) {
        return workflowService.createFromNaturalLanguage(request);
    }

    //screen 3 - details

    @GetMapping("/{id}")
    public Mono<WorkflowResponse> get(@PathVariable String id) {
        return workflowService.findById(id);
    }



}
