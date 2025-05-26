package org.jeff.template.nodes;

import org.jeff.template.RenderContext;
import org.jeff.template.exprs.Expression;

public class NodeForeach extends Node
{
    private final Expression variable;
    private final Expression iter;
    private final NodeBlock body;
    public NodeForeach(Expression variable, Expression iter, NodeBlock body, int line)
    {
        super(line);
        this.variable = variable;
        this.iter = iter;
        this.body = body;
    }

    @Override
    public String render(RenderContext context)
    {
        Object name = variable.eval(context);
        Object iterable = iter.eval(context);
        if(!(iterable instanceof Iterable))
            throw new RuntimeException("Not iterable");
        context.pushVariables(); // 创建local变量作用域
        for(Object item : (Iterable)iterable)
        {
            context.set(name.toString(), item);
            try
            {
                this.body.render(context);
            }catch (ContinueException e)
            {
                continue;
            }
            catch (BreakException e)
            {
                break;
            }
        }
        context.popVariables();
        return "";
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("for %s in %s      #%d\r\n", variable, iter, line));
        sb.append(body);
        return sb.toString();
    }
}
