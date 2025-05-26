package org.jeff.template.exprs;

import org.jeff.template.RenderContext;

public interface Assignable extends Expression
{
    public void set(RenderContext context, Object value);
}
