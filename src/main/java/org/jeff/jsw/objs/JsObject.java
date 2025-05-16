package org.jeff.jsw.objs;

import java.util.Map;

public interface JsObject
{
    Object raw();
    String toString();
    JsObjectType type();

    static JsObject to(Object o)
    {
        if(o instanceof JsObject) return (JsObject) o;
        if(o instanceof Number) return new JsNumber(((Number) o).doubleValue());
        if(o instanceof String) return new JsString((String)o);
        if(o instanceof Boolean) return new JsBool((boolean) o);
        if(o instanceof Iterable)
        {
            JsList v = new JsList();
            for(Object i : (Iterable<? extends Object>) o)
            {
                v.items.add(JsObject.to(i));
            }
            return v;
        }
        if(o instanceof Map)
        {
            JsMap v = new JsMap();
            Map<String, Object> temp = (Map<String, Object>) o;
            for(String key: temp.keySet())
            {
                v.items.put(new JsString(key), JsObject.to(temp.get(key)));
            }
            return v;
        }
        return JsNull.NIL;
    }
}
