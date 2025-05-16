package org.jeff.jsw.objs;

public class JsString implements JsObject
{
    public String value;

    public JsString()
    {

    }

    public JsString(String v)
    {
        this.value = v;
    }

    @Override
    public Object raw()
    {
        return this.value;
    }

    @Override
    public JsObjectType type() {
        return JsObjectType.String;
    }

    @Override
    public String toString() {
        return this.value;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(this == obj) return true;
        if(!(obj instanceof JsString)) return false;
        return this.value.equals(((JsString) obj).value);
    }

    @Override
    public int hashCode()
    {
        return value.hashCode();
    }
}
