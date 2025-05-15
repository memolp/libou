package org.jeff.jsw;

import java.util.HashMap;
import java.util.Map;

public class Env
{
    Map<String, Object> vars = new HashMap<>();
    Map<String, Object> contextVars;

    public Env() {}

    public Env(Env parent)
    {
        this.contextVars = parent.vars;
    }

    public Object get(String name)
    {
        if (vars.containsKey(name)) return vars.get(name);
        return contextVars.get(name);
    }

    public void set(String name, Object val)
    {
        vars.put(name, val);
    }
}
