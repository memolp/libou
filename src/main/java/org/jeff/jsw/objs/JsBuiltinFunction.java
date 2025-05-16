package org.jeff.jsw.objs;

public abstract class JsBuiltinFunction implements JsCallable
{
    @Override
    public Object raw()
    {
        return null;
    }

    @Override
    public String toString()
    {
        return "";
    }

    @Override
    public JsObjectType type()
    {
        return JsObjectType.JAVA_FUNCTION;
    }
}
