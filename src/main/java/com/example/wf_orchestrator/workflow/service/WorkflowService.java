package com.example.wf_orchestrator.workflow.service;

import com.example.wf_orchestrator.workflow.dto.WorkflowRequest;
import com.example.wf_orchestrator.workflow.dto.WorkflowResponse;
import com.example.wf_orchestrator.workflow.dto.WorkflowWithExecutions;
import com.example.wf_orchestrator.workflow.type.WorkflowStatus;
import com.example.wf_orchestrator.workflow.entity.Workflow;
import com.example.wf_orchestrator.workflow.repository.WorkflowExecutionRepository;
import com.example.wf_orchestrator.workflow.repository.WorkflowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.springframework.ai.util.json.JsonParser.toJson;

@Service
@RequiredArgsConstructor
public class WorkflowService {

    private final WorkflowRepository workflowRepo;
    private final WorkflowExecutionRepository executionRepo;
    private final WorkflowParserService parserService;

    public Mono<WorkflowResponse> createFromNaturalLanguage(WorkflowRequest request) {
        return parserService.parse(request.description())
            .flatMap(graph -> {
                var workflow = Workflow.builder()
                    .id(UUID.randomUUID().toString())
                    .name(request.name())
                    .naturalLanguageDescription(request.description())
                    .graphJson(toJson(graph))
                    .status(WorkflowStatus.DRAFT)
                    .build();
                return workflowRepo.save(workflow);
            })
            .map(WorkflowResponse::from);
    }

    public Mono<Page<WorkflowResponse>> findAll(int page, int size, String sortBy, String sortOrder) {
        var sort = sortOrder.equalsIgnoreCase("Desc")?
         Sort.by(sortBy).descending(): Sort.by(sortBy).ascending();
        var pageable = PageRequest.of(page,size,sort);

        return workflowRepo.findAllBy(pageable)
                .map(WorkflowResponse::from)
                .collectList()
                .zipWith(workflowRepo.count())
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }

    public Mono<WorkflowResponse> findById(String id) {
        return workflowRepo.findById(id)
            .switchIfEmpty(Mono.error(
                new IllegalStateException(id)))
            .map(WorkflowResponse::from);
    }

    // Manual joint
    public Mono<WorkflowWithExecutions> findWithExecutions(String id) {
        return workflowRepo.findById(id)
            .zipWith(executionRepo.findByWorkflowId(id).collectList())
            .map(tuple -> new WorkflowWithExecutions(tuple.getT1(), tuple.getT2()));
    }

    public Mono<Void> delete(String id) {
        return workflowRepo.deleteById(id);
    }
}