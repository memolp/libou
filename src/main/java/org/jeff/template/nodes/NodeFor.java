package org.jeff.template.nodes;

import org.jeff.template.RenderContext;
import org.jeff.template.exprs.Expression;

public class NodeFor extends Node
{
    private final Expression init, cond, update;
    private final NodeBlock body;
    public NodeFor(Expression init, Expression cond, Expression update, NodeBlock body, int line)
    {
        super(line);
        this.init = init;
        this.cond = cond;
        this.update = update;
        this.body = body;
    }

    @Override
    public String render(RenderContext context)
    {
        context.pushVariables(); // 建立local作用域
        this.init.eval(context);
        do {
            boolean r = (boolean)this.cond.eval(context);
            if(!r) break;
            try
            {
                this.body.render(context);
            }catch (ContinueException e)
            {
            }catch (BreakException e)
            {
                break;
            }
            this.update.eval(context);
        }while(true);
        context.popVariables();
        return "";
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("for (%s; %s; %s)       #%d\r\n", init, cond, update, line));
        sb.append(body);
        return sb.toString();
    }
}
