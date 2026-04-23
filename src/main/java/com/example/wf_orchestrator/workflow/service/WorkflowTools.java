package com.example.wf_orchestrator.workflow.service;

import com.example.wf_orchestrator.workflow.model.WorkflowEdge;
import com.example.wf_orchestrator.workflow.model.WorkflowGraph;
import com.example.wf_orchestrator.workflow.model.WorkflowStep;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WorkflowTools {

    @Tool(description = "Create a workflow's JSON structure from a description")
    public WorkflowGraph createWorkflow(
        @ToolParam(description = "Workflow steps list") List<WorkflowStep> steps,
        @ToolParam(description = "Connections between steps")  List<WorkflowEdge> edges
    ) {
        // Spring AI generate Json schema from Java types
        // and deserialize the llm answer here
        return new WorkflowGraph(steps, edges);
    }
}