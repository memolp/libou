package org.jeff.template.nodes;

import org.jeff.template.RenderContext;
import org.jeff.template.exprs.Expression;

public class NodeExpr extends Node
{
    private final Expression expr;
    private final boolean decode;
    private Object value = null;

    public NodeExpr(Expression text, int line)
    {
        super(line);
        this.expr = text;
        this.decode = true;
    }

    public NodeExpr(Expression expr, boolean decode, int line)
    {
        super(line);
        this.expr = expr;
        this.decode = decode;
    }

    @Override
    public String render(RenderContext context)
    {
        Object value = expr.eval(context);
        context.echo(value != null ? value.toString() : "");
        return "";
    }
}
