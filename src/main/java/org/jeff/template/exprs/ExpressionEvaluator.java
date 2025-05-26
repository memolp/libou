package org.jeff.template.exprs;

import org.jeff.template.RenderContext;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ExpressionEvaluator
{
    public static Expression build(String code, int line)
    {
        ExpressionParser parser = new ExpressionParser(code, line);
        Expression expr = parser.parse();
        return expr;
    }

    public static Object evaluate(String expr, RenderContext context)
    {
        expr = expr.trim();
        if(expr.startsWith("\"") && expr.endsWith("\""))
        {
            return expr.substring(1, expr.length() - 1);
        }
        if(expr.startsWith("'") && expr.endsWith("'"))
        {
            return expr.substring(1, expr.length() -1);
        }
        if(expr.matches("\\d+"))
        {
            return Integer.parseInt(expr);
        }
        if(expr.contains("(") && expr.endsWith(")"))
        {
            return evaluateFunction(expr, context);
        }
        if(expr.contains("["))
        {
            String[] parts = expr.split("\\[", 2);
            String base = parts[0].trim();
            String keyExpr = parts[1].substring(0, parts[1].length() - 1).trim();
            Object baseObj = context.get(base);
            Object key = evaluate(keyExpr, context);
            if(baseObj instanceof List)
            {
                return ((List<?>)baseObj).get(Integer.parseInt(key.toString()));
            }else if(baseObj instanceof Map)
            {
                return ((Map<?, ?>)baseObj).get(key);
            }
        }
        return context.get(expr);
    }

    private static Object evaluateFunction(String expr, RenderContext context)
    {
        String name = expr.substring(0, expr.indexOf("(")).trim();
        String args = expr.substring(expr.indexOf("(") + 1, expr.lastIndexOf(")")).trim();
        Object target = context.get(name);
        if(target instanceof Method)
        {
            //
        }
        if("len".equals(name))
        {
            Object arg = evaluate(args, context);
            if(arg instanceof Collection) return ((Collection<?>)arg).size();
            if(arg instanceof Map) return ((Map<?, ?>)arg).size();
            if(arg instanceof String) return ((String)arg).length();
        }
        return "";
    }

    public static boolean evaluatieCondition(String cond, RenderContext context)
    {
        cond = cond.trim();
        if(cond.contains("=="))
        {
            String[] parts = cond.split("==");
            return evaluate(parts[0], context).toString().equals(evaluate(parts[1], context).toString());
        }else if(cond.contains("<"))
        {
            String[] parts = cond.split("<");
            System.out.println("parts[0] = " + parts[0]);
            System.out.println("parts[1] = " + parts[1]);
            int left = Integer.parseInt(evaluate(parts[0], context).toString());
            int right = Integer.parseInt(evaluate(parts[1], context).toString());
            return  left < right;
        }else if(cond.contains(">"))
        {
            String[] parts = cond.split(">");
            return Integer.parseInt(evaluate(parts[0], context).toString()) > Integer.parseInt(evaluate(parts[1], context).toString());
        }else if(cond.contains("!="))
        {
            String[] parts = cond.split("!=");
            return !evaluate(parts[0], context).toString().equals(evaluate(parts[1], context).toString());
        }
        return Boolean.parseBoolean(evaluate(cond, context).toString());
    }
}
