package org.jeff.jsw.exprs;

import org.jeff.jsw.JsContext;
import org.jeff.jsw.objs.JsObject;

public interface Expression
{
    JsObject eval(JsContext context);
}
