package org.jeff.jsw.objs;

public class JsNull implements JsObject
{
    public static final JsNull NIL = new JsNull();

    private JsNull(){}

    @Override
    public Object raw() {
        return null;
    }

    @Override
    public JsObjectType type() {
        return JsObjectType.NIL;
    }

    @Override
    public String toString()
    {
        return "null";
    }
}
