package org.jeff.jsw.exprs;

import org.jeff.jsw.JsContext;
import org.jeff.jsw.objs.JsObject;

public class VarExpr implements Assignable
{
    private final String name;

    public VarExpr(String name) {
        this.name = name;
    }

    public JsObject eval(JsContext context)
    {
        return context.get(name);
    }

    @Override
    public void assign(JsContext context, JsObject value)
    {
        context.set(name, value);
    }

    @Override
    public String toString() {
        return "[VarExpr]:" + name;
    }
}
