package com.example.wf_orchestrator.workflow.service;

import com.example.wf_orchestrator.workflow.model.WorkflowGraph;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@Slf4j
@RequiredArgsConstructor
public class WorkflowParserService {

    private final ChatClient chatClient;

    private final WorkflowTools workflowTools;

    //parse the user input and call the llm
    public Mono<WorkflowGraph> parse(String input) {
        return Mono.fromCallable(() ->
                chatClient.prompt()
                        .user(input)
                        .tools(workflowTools)
                        .call()
                        .entity(WorkflowGraph.class)
        ).subscribeOn(Schedulers.boundedElastic());
    }
}