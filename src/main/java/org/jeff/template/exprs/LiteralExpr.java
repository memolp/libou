package org.jeff.template.exprs;

import org.jeff.template.RenderContext;

public class LiteralExpr implements Expression
{
    private final Object literal;

    public LiteralExpr(Object v)
    {
        this.literal = v;
    }

    @Override
    public Object eval(RenderContext context)
    {
        return this.literal;
    }
}
