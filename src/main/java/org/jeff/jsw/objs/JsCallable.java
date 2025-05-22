package org.jeff.jsw.objs;

import org.jeff.jsw.JsContext;

public interface JsCallable extends JsObject
{
    JsObject call(JsContext context, JsObject... args);
}
