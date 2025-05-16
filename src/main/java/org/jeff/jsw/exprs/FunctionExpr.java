package org.jeff.jsw.exprs;

import org.jeff.jsw.JsContext;
import org.jeff.jsw.objs.JsFunction;
import org.jeff.jsw.objs.JsNull;
import org.jeff.jsw.objs.JsObject;
import org.jeff.jsw.statements.BlockStatement;

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
    public JsObject eval(JsContext jsContext)
    {
        return new JsFunction("", this);
    }

    public JsObject call(JsContext context, JsObject...args)
    {
        JsContext local = new JsContext(context);
        for(int i = 0; i < params.size(); i++)
        {
            if(i < args.length)
                local.set(params.get(i), args[i]);
            else
                local.set(params.get(i), JsNull.NIL);
        }
        return body.execute(local);
    }

    @Override
    public String toString()
    {
        return "[FuncExpr]:" + params.toString() + "{" + body.toString() + "}";
    }
}
