package org.jeff.jsw.exprs;

import org.jeff.jsw.Env;

public class VarExpr implements Expression
{
    private final String name;

    public VarExpr(String name) {
        this.name = name;
    }

    public Object eval(Env env) {
        return env.get(name);
    }

    @Override
    public String toString() {
        return "[VarExpr]:" + name;
    }
}
