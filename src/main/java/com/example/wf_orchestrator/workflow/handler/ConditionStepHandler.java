package com.example.wf_orchestrator.workflow.handler;

import com.example.wf_orchestrator.workflow.type.StepHandler;
import com.example.wf_orchestrator.workflow.type.StepType;
import com.example.wf_orchestrator.workflow.model.ExecutionContext;
import com.example.wf_orchestrator.workflow.model.StepResult;
import com.example.wf_orchestrator.workflow.model.WorkflowStep;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class ConditionStepHandler implements StepHandler {

    private final ExpressionParser parser = new SpelExpressionParser();

    @Override
    public StepType getType() {
        return StepType.CONDITION;
    }

    @Override
    public Mono<StepResult> execute(WorkflowStep step, ExecutionContext context) {
        String expression = getConfig(step, "expression");

        if (expression == null || expression.isBlank()) {
            return Mono.just(StepResult.fail("expression is required for CONDITION"));
        }

        try {

            StandardEvaluationContext evalContext = new StandardEvaluationContext();


            evalContext.setVariable("inputs", context.inputs());
            evalContext.setVariable("variables", context.variables());


            Expression exp = parser.parseExpression(expression);
            Boolean result = exp.getValue(evalContext, Boolean.class);

            if (result == null) {
                return Mono.just(StepResult.fail("Expression must return a boolean: " + expression));
            }

            return Mono.just(StepResult.ok(Map.of(
                "conditionResult", result
            )));

        } catch (Exception e) {
            return Mono.just(StepResult.fail("Error evaluating expression '" + expression + "': " + e.getMessage()));
        }
    }

    private <T> T getConfig(WorkflowStep step, String key) {
        return (T) step.config().get(key);
    }
}
