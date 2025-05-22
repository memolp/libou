package org.jeff.jsw.statements;

import org.jeff.jsw.JsContext;
import org.jeff.jsw.exprs.Expression;
import org.jeff.jsw.objs.JsNull;
import org.jeff.jsw.objs.JsObject;

public class LetStatement implements Statement
{
    private final String name;
    private final Expression expr;

    public LetStatement(String name, Expression expr) {
        this.name = name;
        this.expr = expr;
    }

    public JsObject execute(JsContext context)
    {
        if(expr != null)
            context.set(name, expr.eval(context));
        else
            context.set(name, JsNull.NIL);
        return null;
    }

    @Override
    public String toString()
    {
        return "[LetStatement]: " + name + " = " + expr.toString();
    }
}
