package org.jeff.jsw.statements;

import org.jeff.jsw.Env;
import org.jeff.jsw.exprs.FunctionExpr;

import java.util.List;

public class FunctionStatement implements Statement
{
    String funcName;
    FunctionExpr functionExpr;

    public FunctionStatement(String funcName, List<String> params, BlockStatement body)
    {
        this.funcName = funcName;
        this.functionExpr = new FunctionExpr(params, body);
    }

    @Override
    public Object execute(Env env, Object...args)
    {
        env.set(funcName, functionExpr);
        return null;
    }

    @Override
    public String toString()
    {
        return "[FunctionStatement]:" + funcName + "\n" + functionExpr.toString();
    }
}
