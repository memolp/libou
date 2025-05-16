package org.jeff.jsw.exprs;

import org.jeff.jsw.JsContext;
import org.jeff.jsw.objs.JsObject;

public interface Assignable extends Expression
{
    void assign(JsContext context, JsObject value);
}
