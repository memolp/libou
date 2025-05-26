package org.jeff.template.exprs;

import org.jeff.template.RenderContext;
import org.jeff.template.nodes.BreakException;

public class BreakExpr implements Expression
{
    public BreakExpr()
    {

    }
    @Override
    public Object eval(RenderContext context)
    {
        throw new BreakException();
    }
}
