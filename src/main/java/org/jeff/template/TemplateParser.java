package org.jeff.template;

import org.jeff.template.exprs.Expression;
import org.jeff.template.exprs.ExpressionEvaluator;
import org.jeff.template.exprs.VariableExpr;
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

    private static void parseLine(String rawLine, int line,  Stack<NodeBlock> stack)
    {
        int index = 0;
        while(index < rawLine.length())
        {
            int startTag = rawLine.indexOf("{%", index);
            int startExp = rawLine.indexOf("{{", index);
            int next = -1;
            boolean isExpr = false;
            if(startTag != -1 && (startExp == -1 || startExp > startTag))
            {
                next = startTag;
            }else if(startExp != -1)
            {
                next = startExp;
                isExpr = true;
            }
            if(next == -1)  // 当前行什么都没有
            {
                String text = rawLine.substring(index);
                addNode(new NodeText(text, line), stack);
                break;
            }else
            {
                if(next > index)  // 当前行存在脚本，但是前面还有一个部分文本
                {
                    String text = rawLine.substring(index, next);
                    addNode(new NodeText(text, line), stack);
                }
                if(isExpr)
                {
                    int end = rawLine.indexOf("}}", next);
                    if(end == -1) throw new RuntimeException("");
                    Expression expr = ExpressionEvaluator.build(rawLine.substring(next + 2, end).trim(), line);
                    addNode(new NodeExpr(expr, line), stack);
                    index = end + 2;
                }else
                {
                    int end = rawLine.indexOf("%}", next);
                    if(end == -1) throw new RuntimeException("");
                    String stmt = rawLine.substring(next +2, end).trim();
                    if(stmt.startsWith("if"))
                    {
                        Expression cond = ExpressionEvaluator.build(stmt.substring(2), line);
                        NodeBlock block = new NodeBlock("", line);
                        NodeBranch branch = new NodeBranch(cond, block, line);
                        addNode(branch, stack);
                        stack.push(block);
                    }else if(stmt.startsWith("elif"))
                    {
                        Expression cond = ExpressionEvaluator.build(stmt.substring(4), line);
                        NodeBlock block = new NodeBlock("", line);
                        NodeBranch branch = new NodeBranch(cond, block, line);
                        addNode(branch, stack);
                        stack.push(block);
                    }else if(stmt.startsWith("else"))
                    {
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
                    }else
                    {
                        throw new RuntimeException("unexpected code in line:" + line);
                    }
                    index = end + 2;
                }
            }
        }
    }

    private static void addNode(Node node, Stack<NodeBlock> stack)
    {
        stack.peek().addChild(node);
    }

}
