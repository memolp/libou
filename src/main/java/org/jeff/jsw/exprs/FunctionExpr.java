package org.jeff.jsw.exprs;

import org.jeff.jsw.Env;
import org.jeff.jsw.statements.BlockStatement;
import org.jeff.jsw.statements.Statement;

import java.util.List;

public class FunctionExpr implements Expression
{
    List<String> params;
    BlockStatement body;
    public FunctionExpr(List<String> params, BlockStatement body)
    {
        this.params = params;
        this.body = body;
    }

    @Override
    public Object eval(Env env)
    {
        Env local = new Env(env);
        return this.body.execute(local);
    }

    public Object eval(Env env, Object...args)
    {
        Env local = new Env(env);
        return this.body.execute(local, args);
    }

    @Override
    public String toString()
    {
        return "[FuncExpr]:" + params.toString() + "{" + body.toString() + "}";
    }
}
