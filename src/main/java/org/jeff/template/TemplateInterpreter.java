package org.jeff.template;

import org.jeff.template.exprs.ExpressionEvaluator;
import org.jeff.template.nodes.Node;

import java.util.List;

class TemplateInterpreter
{
    public static String render(List<Node> nodes, RenderContext context)
    {
        StringBuilder sb = new StringBuilder();
        for(Node node : nodes)
        {
            sb.append(node.render(context));
            if(context.isBreakFlag()) break;
            if(context.isContinueFlag())
            {
                context.clearContinue();
                break;
            }
        }
        return sb.toString();
    }

    public static String evalStatement(String statement, RenderContext context)
    {
        if(statement.startsWith("set"))
        {
            String[] parts = statement.substring(4).split("=", 2);
            String var = parts[0].trim();
            String valExp = parts[1].trim();
            Object value = ExpressionEvaluator.evaluate(valExp, context);
            context.set(var, value);
            return "";
        }else if(statement.startsWith("if"))
        {
            context.pushIf(ExpressionEvaluator.evaluatieCondition(statement.substring(3), context));
            return "";
        }else if(statement.startsWith("elif"))
        {
            context.popIf();
            context.pushIf(ExpressionEvaluator.evaluatieCondition(statement.substring(5), context));
            return "";
        }else if(statement.equals("else"))
        {
            context.popIf();
            context.pushIf(true);
            return "";
        }else if(statement.equals("end"))
        {
            context.popIf();
            return "";
        }else if(statement.equals("break"))
        {
            context.setBreakFlag(true);
            return "";
        }else if(statement.equals("continue"))
        {
            context.setContinueFlag(true);
            return "";
        }else if(statement.startsWith("for"))
        {
            return loop(statement.substring(4), context);
        }else if(statement.contains("="))
        {
            String[] parts = statement.split("=", 2);
            String var = parts[0].trim();
            String valExp = parts[1].trim();
            Object value = ExpressionEvaluator.evaluate(valExp, context);
            context.set(var, value);
            return "";
        }
        return "";
    }

    private static String loop(String stmt, RenderContext context)
    {
        String[] parts = stmt.split(";");
        if(parts.length != 3) throw new RuntimeException("Invalid for statement");
        String init = parts[0].trim();
        String cond = parts[1].trim();
        String update = parts[2].trim();
        StringBuilder sb = new StringBuilder();
        TemplateInterpreter.evalStatement(init, context);
        while(ExpressionEvaluator.evaluatieCondition(cond, context))
        {
            for(Node node: context.getBody())
            {
                sb.append(node.render(context));
                if(context.isBreakFlag())
                {
                    context.clearBreak();
                    return sb.toString();
                }
                if(context.isContinueFlag())
                {
                    context.clearContinue();
                    break;
                }
            }
            TemplateInterpreter.evalStatement(update, context);
        }
        return sb.toString();
    }
}
