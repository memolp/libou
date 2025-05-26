package org.jeff.template.exprs;


import org.jeff.template.RenderContext;

public interface Expression
{
    Object eval(RenderContext context);
}
