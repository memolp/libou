package org.jeff.template.nodes;

import org.jeff.template.RenderContext;
import org.jeff.template.exprs.Expression;

public class NodeBranch extends Node
{
    private final Expression cond;
    private final NodeBlock body;
    public NodeBranch(Expression cond, NodeBlock body, int line)
    {
        super(line);
        this.cond = cond;
        this.body = body;
    }

    @Override
    public String render(RenderContext context)
    {
        if(this.cond != null)
        {
            boolean v = (boolean)this.cond.eval(context);
            if(!v) return "";
        }
        context.pushVariables();
        this.body.render(context);
        context.popVariables();
        return "";
    }

    @Override
    public String toString()
    {
        return super.toString();
    }
}
