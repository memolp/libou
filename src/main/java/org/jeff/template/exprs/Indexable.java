package org.jeff.template.exprs;

import org.jeff.template.RenderContext;

public interface Indexable
{
    Object get(Object index);
    void set(Object index, Object value);
}
