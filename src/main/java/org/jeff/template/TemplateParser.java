package org.jeff.template;

import org.jeff.template.exprs.*;
import org.jeff.template.nodes.*;

import java.util.Stack;

public class TemplateParser
{
    public static NodeBlock parse(String template)
    {
        return parse(template.split("\r\n"));
    }

    public static NodeBlock parse(Iterable<String> lines)
    {
        int line = 1;
        NodeBlock topLevel = new NodeBlock("", line);
        Stack<NodeBlock> stack = new Stack<>();
        stack.push(topLevel);
        for(String rawLine : lines)
        {
            parseLine(rawLine, line, stack);
            line ++;
        }
        if(stack.size() != 1) throw new RuntimeException("error");
        return topLevel;
    }

    public static NodeBlock parse(String[] lines)
    {
        int line = 1;
        NodeBlock topLevel = new NodeBlock("", line);
        Stack<NodeBlock> stack = new Stack<>();
        stack.push(topLevel);
        for(String rawLine : lines)
        {
            parseLine(rawLine, line, stack);
            line ++;
        }
        if(stack.size() != 1) throw new RuntimeException("error");
        return topLevel;
    }

    private static void addTextNode(String text, int line, Stack<NodeBlock> stack)
    {
        text = text.trim();
        if(text.isEmpty()) return;  // 过滤掉空内容
        NodeText node = new NodeText(text, line);
        addNode(node, stack);
    }

    private static void addExprNode(String expr, int line, Stack<NodeBlock> stack)
    {
        if(expr.trim().isEmpty()) throw new RuntimeException("{{  }}");
        Expression expression = ExpressionEvaluator.build(expr, line);
        NodeExpr node = new NodeExpr(expression, line);
        addNode(node, stack);
    }

    private static void addStatementNode(String stmt, int line, Stack<NodeBlock> stack)
    {
        if(stmt.startsWith("if"))
        {
            Expression cond = ExpressionEvaluator.build(stmt.substring(2), line);
            NodeBlock block = new NodeBlock("", line);
            NodeBranch branch = new NodeBranch(cond, block, line);
            addNode(branch, stack);
            stack.push(block);
        }else if(stmt.startsWith("elif"))
        {
            stack.pop();
            Expression cond = ExpressionEvaluator.build(stmt.substring(4), line);
            NodeBlock block = new NodeBlock("", line);
            NodeBranch branch = new NodeBranch(cond, block, line);
            addNode(branch, stack);
            stack.push(block);
        }else if(stmt.startsWith("else"))
        {
            stack.pop();
            NodeBlock block = new NodeBlock("", line);
            NodeBranch branch = new NodeBranch(null, block, line);
            addNode(branch, stack);
            stack.push(block);
        }
        else if(stmt.startsWith("for"))
        {
            stmt = stmt.substring(3).trim();
            if(stmt.contains("in"))
            {
                String[] parts = stmt.split("in");
                Expression v = new VariableExpr(parts[0].trim());
                Expression iter = ExpressionEvaluator.build(parts[1].trim(), line);
                NodeBlock body = new NodeBlock("", line);
                NodeForeach foreach = new NodeForeach(v, iter, body, line);
                addNode(foreach, stack);
                stack.push(body);
            }else
            {
                String[] parts = stmt.split(";");
                if(parts.length != 3) throw new RuntimeException("");
                Expression init = ExpressionEvaluator.build(parts[0].trim(), line);
                Expression cond = ExpressionEvaluator.build(parts[1].trim(), line);
                Expression update = ExpressionEvaluator.build(parts[2].trim(), line);
                NodeBlock body = new NodeBlock("", line);
                NodeFor forStmt = new NodeFor(init, cond, update, body, line);
                addNode(forStmt, stack);
                stack.push(body);
            }
        } else if(stmt.equals("end"))
        {
            if(stack.size() ==1) throw new RuntimeException("");
            stack.pop();
        } else if(stmt.startsWith("set"))
        {
            Expression expr = ExpressionEvaluator.build(stmt.substring(3), line);
            addNode(new NodeVariable(expr, line), stack);
        } else if(stmt.startsWith("raw"))
        {
            Expression expr = ExpressionEvaluator.build(stmt.substring(3), line);
            addNode(new NodeExpr(expr, true, line), stack);
        } else if(stmt.equals("break"))
        {
            Expression expr = new BreakExpr();
            addNode(new NodeExpr(expr, true, line), stack);
        }else if(stmt.equals("continue"))
        {
            Expression expr = new ContinueExpr();
            addNode(new NodeExpr(expr, true, line), stack);
        }
        else
        {
            throw new RuntimeException("unexpected code in line:" + line);
        }
    }

    private static void parseLine(String rawLine, int line,  Stack<NodeBlock> stack)
    {
        int index = 0;
        while(index < rawLine.length())
        {
            int brace = rawLine.indexOf("{", index);
            if(brace == -1)  // 当前行没有{
            {
                addTextNode(rawLine.substring(index), line, stack);
                break;
            }
            if(brace > index)  // {之前的内容
            {
                addTextNode(rawLine.substring(index, brace), line, stack);
            }
            if(brace + 1 >= rawLine.length())  // 末尾的孤立{
            {
                addTextNode("{", line, stack);
                break;
            }
            char nextChar = rawLine.charAt(brace + 1);
            if(nextChar == '{')  // {{ 表达式
            {
                int end = rawLine.indexOf("}}", brace + 2);
                if(end == -1) throw new RuntimeException("}} not found at line:" + line);
                addExprNode(rawLine.substring(brace + 2, end).trim(), line, stack);
                index = end + 2;
            }else if(nextChar == '%')  // {% 语句
            {
                int end = rawLine.indexOf("%}", brace + 2);
                if(end == -1) throw new RuntimeException("%} not found at line:" + line);
                String stmt = rawLine.substring(brace + 2, end).trim();
                addStatementNode(stmt, line, stack);
                index = end + 2;
            }else if(nextChar == '#')  // {# 注释
            {
                int end = rawLine.indexOf("#}", index);
                if(end == -1) throw new RuntimeException("#} not found at line:" + line);
                index = end + 2;
            }else  // 其他的{不理
            {
                addTextNode("{", line, stack);
                index = brace + 1;
            }
        }
        // 如果需要看换行的效果
        //addNode(new NodeText("\r\n", line), stack);
    }

    private static void addNode(Node node, Stack<NodeBlock> stack)
    {
        stack.peek().addChild(node);
    }

}
