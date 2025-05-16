package org.jeff.jsw.objs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class JsMap implements JsIndexable, Iterable<JsObject>
{
    public Map<JsObject, JsObject> items;
    public JsMap()
    {
        this.items = new HashMap<>();
    }
    public JsMap(Map<JsObject, JsObject> items)
    {
        this.items = items;
    }

    @Override
    public Object raw()
    {
        return items;
    }

    @Override
    public JsObjectType type()
    {
        return JsObjectType.MAP;
    }

    @Override
    public String toString()
    {
        return String.valueOf(items);
    }

    @Override
    public Iterator<JsObject> iterator()
    {
        return this.items.keySet().iterator();
    }

    @Override
    public JsObject get(JsObject index)
    {
        return this.items.get(index);
    }

    @Override
    public void set(JsObject index, JsObject value)
    {
        this.items.put(index, value);
    }
}
