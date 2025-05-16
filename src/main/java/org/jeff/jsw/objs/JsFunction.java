package org.jeff.jsw.objs;

import org.jeff.jsw.JsContext;
import org.jeff.jsw.exprs.FunctionExpr;

public class JsFunction implements JsCallable
{
    private FunctionExpr f;
    private String name;

    public JsFunction(String name, FunctionExpr expr)
    {
        this.name = name;
        this.f = expr;
    }

    @Override
    public JsObject call(JsContext ctx, JsObject... args)
    {
        return f.call(ctx, args);
    }

    @Override
    public Object raw()
    {
        return this;
    }

    @Override
    public JsObjectType type() {
        return JsObjectType.FUNCTION;
    }

    @Override
    public String toString()
    {
        return "<function:" + name + ">";
    }
}
