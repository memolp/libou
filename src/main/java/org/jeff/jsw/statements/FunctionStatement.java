package org.jeff.jsw.statements;

import org.jeff.jsw.JsContext;
import org.jeff.jsw.exprs.FunctionExpr;
import org.jeff.jsw.objs.JsFunction;
import org.jeff.jsw.objs.JsObject;

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
    public JsObject execute(JsContext jsContext)
    {
        JsFunction func = new JsFunction(funcName, functionExpr);
        jsContext.set(funcName, func);
        return null;
    }

    @Override
    public String toString()
    {
        return "[FunctionStatement]:" + funcName + "\n" + functionExpr.toString();
    }
}
