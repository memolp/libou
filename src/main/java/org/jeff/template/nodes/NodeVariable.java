package org.jeff.template.nodes;

import org.jeff.template.RenderContext;
import org.jeff.template.exprs.Expression;

public class NodeVariable extends Node
{
    private final Expression expr;
    public NodeVariable(Expression expr, int line)
    {
        super(line);
        this.expr = expr;
    }

    @Override
    public String render(RenderContext context)
    {
        Object value = expr.eval(context);
        context.echo(value != null ? value.toString() : "");
        return "";
    }
}
