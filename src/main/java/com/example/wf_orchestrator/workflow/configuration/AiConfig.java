package com.example.wf_orchestrator.workflow.configuration;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    @Bean
    public ChatClient chatClient(OllamaChatModel ollamaChatModel) {
        return ChatClient.builder(ollamaChatModel)
            .defaultSystem("""
                You are a workflow prser. Since a description in natural language,
                generate the corresponding structured graph.
                answer ONLY by calling the tool create_workflow.
                /no_think
                """)
            .build();
    }
}