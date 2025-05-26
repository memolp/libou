package org.jeff.template;

import org.jeff.template.exprs.*;
import org.jeff.template.nodes.*;

import java.util.Stack;

public class TemplateParser
{
    public static CodeNode parse(String template)
    {
        return parse(template.split("\r\n"));
    }

    public static CodeNode parse(Iterable<String> lines)
    {
        int line = 1;
        CodeNode topLevel = new CodeNode(line);
        Stack<BlockNode> stack = new Stack<>();
        stack.push(topLevel);
        for(String rawLine : lines)
        {
            parseLine(rawLine, line, stack);
            line ++;
        }
        if(stack.size() != 1) throw new RuntimeException(stack.peek().toString() + "not found {%end%}");
        return topLevel;
    }

    public static CodeNode parse(String[] lines)
    {
        int line = 1;
        CodeNode topLevel = new CodeNode(line);
        Stack<BlockNode> stack = new Stack<>();
        stack.push(topLevel);
        for(String rawLine : lines)
        {
            parseLine(rawLine, line, stack);
            line ++;
        }
        if(stack.size() != 1) throw new RuntimeException("error");
        return topLevel;
    }

    private static void addTextNode(String text, int line, Stack<BlockNode> stack)
    {
        text = text.trim();
        if(text.isEmpty()) return;  // 过滤掉空内容
        TextNode node = new TextNode(text, line);
        addNode(node, stack);
    }

    private static void addExprNode(String expr, int line, Stack<BlockNode> stack)
    {
        if(expr.trim().isEmpty()) throw new RuntimeException("{{  }}");
        Expression expression = ExpressionEvaluator.build(expr, line);
        ExpressionNode node = new ExpressionNode(expression, line);
        addNode(node, stack);
    }

    private static void addStatementNode(String stmt, int line, Stack<BlockNode> stack)
    {
        if(stmt.startsWith("if"))
        {
            Expression cond = ExpressionEvaluator.build(stmt.substring(2), line);
            BranchNode branch = new BranchNode(BranchNode.BRANCH_IF, cond, line);
            addNode(branch, stack);
            stack.push(branch);
        }else if(stmt.startsWith("elif"))
        {
            BlockNode n = stack.pop();
            if(!(n.blockType == BlockNode.CODE_IF || n.blockType == BlockNode.CODE_ELIF))
            {
                throw new RuntimeException(String.format("elif must after if or elif line:%d", line));
            }
            Expression cond = ExpressionEvaluator.build(stmt.substring(4), line);
            BranchNode branch = new BranchNode(BranchNode.BRANCH_ELIF, cond, line);
            addNode(branch, stack);
            stack.push(branch);
        }else if(stmt.startsWith("else"))
        {
            BlockNode n = stack.pop();
            if(!(n.blockType == BlockNode.CODE_IF || n.blockType == BlockNode.CODE_ELIF))
            {
                throw new RuntimeException(String.format("else must after if or elif line:%d", line));
            }
            BranchNode branch = new BranchNode(BranchNode.BRANCH_ELSE,null, line);
            addNode(branch, stack);
            stack.push(branch);
        }
        else if(stmt.startsWith("for"))
        {
            stmt = stmt.substring(3).trim();
            if(stmt.contains("in"))
            {
                String[] parts = stmt.split("in");
                Expression v = new VariableExpr(parts[0].trim());
                Expression iter = ExpressionEvaluator.build(parts[1].trim(), line);
                ForeachNode foreach = new ForeachNode(v, iter, line);
                addNode(foreach, stack);
                stack.push(foreach);
            }else
            {
                String[] parts = stmt.split(";");
                if(parts.length != 3) throw new RuntimeException("");
                Expression init = ExpressionEvaluator.build(parts[0].trim(), line);
                Expression cond = ExpressionEvaluator.build(parts[1].trim(), line);
                Expression update = ExpressionEvaluator.build(parts[2].trim(), line);
                ForNode forStmt = new ForNode(init, cond, update, line);
                addNode(forStmt, stack);
                stack.push(forStmt);
            }
        } else if(stmt.equals("end"))
        {
            if(stack.size() ==1) throw new RuntimeException("{%end%} unexpected at line:" + line);
            stack.pop();
        } else if(stmt.startsWith("set"))
        {
            Expression expr = ExpressionEvaluator.build(stmt.substring(3), line);
            addNode(new VariableNode(expr, line), stack);
        } else if(stmt.startsWith("raw"))
        {
            Expression expr = ExpressionEvaluator.build(stmt.substring(3), line);
            addNode(new ExpressionNode(expr, true, line), stack);
        } else if(stmt.equals("break"))
        {
            // TODO 这种需要检测stack里面有没有循环
            Expression expr = new BreakExpr();
            addNode(new ExpressionNode(expr, true, line), stack);
        }else if(stmt.equals("continue"))
        {
            // TODO 这种需要检测stack里面有没有循环
            Expression expr = new ContinueExpr();
            addNode(new ExpressionNode(expr, true, line), stack);
        }
        else
        {
            throw new RuntimeException("unexpected code in line:" + line);
        }
    }

    private static void parseLine(String rawLine, int line,  Stack<BlockNode> stack)
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

    private static void addNode(Node node, Stack<BlockNode> stack)
    {
        stack.peek().addChild(node);
    }

}
