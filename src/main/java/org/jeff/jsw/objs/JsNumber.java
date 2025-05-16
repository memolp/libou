package org.jeff.jsw.objs;

public class JsNumber implements JsObject
{
    public double value = 0;
    public JsNumber()
    {

    }
    public JsNumber(double v)
    {
        this.value = v;
    }

    public JsNumber(int v)
    {
        this.value = v;
    }

    @Override
    public Object raw()
    {
        return this.value;
    }

    @Override
    public JsObjectType type()
    {
        return JsObjectType.Number;
    }

    @Override
    public String toString()
    {
        return Double.toString(value);
    }

    public int intValue()
    {
        return (int)value;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(obj == this) return true;
        if(!(obj instanceof JsNumber)) return false;
        return this.value == ((JsNumber) obj).value;
    }

    @Override
    public int hashCode()
    {
        return this.raw().hashCode();
    }
}
