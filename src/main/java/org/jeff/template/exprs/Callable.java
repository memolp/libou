package org.jeff.template.exprs;

import org.jeff.template.RenderContext;

public interface Callable
{
    public Object call(RenderContext context, Object... args);
}
