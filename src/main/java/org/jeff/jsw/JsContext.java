package org.jeff.jsw;

import org.jeff.jsw.objs.*;

import java.util.HashMap;
import java.util.Map;

public class JsContext
{
    Map<String, JsObject> vars = new HashMap<>();
    Map<String, JsObject> contextVars;
    JsContext parent = null;

    public JsContext() {}

    public JsContext(JsContext parent)
    {
        this.parent = parent;
    }

    public JsObject get(String name)
    {
        if (vars.containsKey(name)) return vars.get(name);
        if(contextVars != null && contextVars.containsKey(name)) return contextVars.get(name);
        if(this.parent == null) return JsNull.NIL;
        return this.parent.get(name);
    }

    public void set(String name, JsObject val)
    {
        vars.put(name, val);
    }

    public void set(String name, Object val)
    {
        if(val instanceof JsObject) this.set(name, (JsObject) val);
        this.set(name, JsObject.to(val));
    }

}
