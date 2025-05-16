package org.jeff.jsw.exprs;

import org.jeff.jsw.JsContext;
import org.jeff.jsw.objs.JsCallable;
import org.jeff.jsw.objs.JsObject;

import java.util.List;

public class FunctionCallExpr implements Expression
{
    Expression funcName;
    List<Expression> arguments;
    public FunctionCallExpr(Expression funcName, List<Expression> args)
    {
        this.funcName = funcName;
        this.arguments = args;
    }
    @Override
    public JsObject eval(JsContext jsContext)
    {
        JsObject v = funcName.eval(jsContext);
        if(!(v instanceof JsCallable))
            throw new RuntimeException("Undefined function: " + funcName.toString());

        JsObject[] arguments = new JsObject[this.arguments.size()];
        for (int i = 0; i < this.arguments.size(); i++)
        {
            JsObject t = this.arguments.get(i).eval(jsContext);
            arguments[i] = t;
        }
        JsCallable f = (JsCallable) v;
        return f.call(jsContext, arguments);
    }

    @Override
    public String toString() {
        return "[FuncCallExp]:" + funcName.toString() + "(" + arguments.toString() + ")";
    }
}
