package org.jeff.jsw.objs;

public class JsBool implements JsObject
{
    public boolean value;

    public JsBool()
    {
        this.value = false;
    }

    public JsBool(boolean value)
    {
        this.value = value;
    }

    @Override
    public Object raw()
    {
        return value;
    }

    @Override
    public JsObjectType type()
    {
        return JsObjectType.BOOL;
    }

    @Override
    public String toString()
    {
        return Boolean.toString(value);
    }
}
