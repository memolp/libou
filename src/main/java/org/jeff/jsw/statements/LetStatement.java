package org.jeff.jsw.statements;

import org.jeff.jsw.Env;
import org.jeff.jsw.exprs.Expression;

public class LetStatement implements Statement
{
    private final String name;
    private final Expression expr;

    public LetStatement(String name, Expression expr) {
        this.name = name;
        this.expr = expr;
    }

    public Object execute(Env env, Object ...args)
    {
        Object val = null;
        if(expr != null) val = expr.eval(env);
        env.set(name, val);
        return null;
    }

    @Override
    public String toString()
    {
        return "[LetStatement]: " + name + " = " + expr.toString();
    }
}
