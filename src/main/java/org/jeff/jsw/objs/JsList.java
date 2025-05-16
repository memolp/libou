package org.jeff.jsw.objs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JsList implements JsIndexable, Iterable<JsObject>
{
    public List<JsObject> items;
    public JsList()
    {
        items = new ArrayList<JsObject>();
    }

    public JsList(List<JsObject> items)
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
        return JsObjectType.LIST;
    }

    @Override
    public String toString()
    {
        return String.valueOf(this.items);
    }

    @Override
    public Iterator<JsObject> iterator() {
        return this.items.iterator();
    }

    @Override
    public JsObject get(JsObject index)
    {
        if(index.type() != JsObjectType.Number)
            throw new RuntimeException("Index must be a number");
        int idx = ((JsNumber)index).intValue();
        return this.items.get(idx);
    }

    @Override
    public void set(JsObject index, JsObject value)
    {
        if(index.type() != JsObjectType.Number)
            throw new RuntimeException("Index must be a number");
        int idx = ((JsNumber)index).intValue();
        this.items.set(idx, value);
    }
}
