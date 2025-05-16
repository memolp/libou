package org.jeff.jsw.objs;

public interface JsIndexable
{
    JsObject get(JsObject index);
    void set(JsObject index, JsObject value);
}
