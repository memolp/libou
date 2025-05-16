package org.jeff.jsw.objs;

public interface JsIndexable extends JsObject
{
    JsObject get(JsObject index);
    void set(JsObject index, JsObject value);
}
