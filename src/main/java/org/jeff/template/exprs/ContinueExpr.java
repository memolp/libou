package org.jeff.template.exprs;

import org.jeff.template.RenderContext;
import org.jeff.template.nodes.ContinueException;

public class ContinueExpr implements Expression
{
    public ContinueExpr()
    {

    }

    @Override
    public Object eval(RenderContext context)
    {
        throw new ContinueException();
    }
}
