package org.jeff.jsw.exprs;

import org.jeff.jsw.BuiltinFunction;
import org.jeff.jsw.Env;

import java.util.ArrayList;
import java.util.List;

public class FunctionCallExpr implements Expression
{
    Expression funcName;
    List<Expression> params;
    public FunctionCallExpr(Expression funcName, List<Expression> params)
    {
        this.funcName = funcName;
        this.params = params;
    }
    @Override
    public Object eval(Env env) {
        Object v = funcName.eval(env);
        if (v == null) throw new RuntimeException("Undefined function: " + funcName.toString());
        List<Object> args = new ArrayList<Object>();
        for (Expression p : params) {
            args.add(p.eval(env));
        }
        if (v instanceof FunctionExpr) {
            FunctionExpr f = FunctionExpr.class.cast(v);
            return f.eval(env, args.toArray());
        } else if (v instanceof BuiltinFunction)
        {
            BuiltinFunction f = BuiltinFunction.class.cast(v);
            return f.call(env, args.toArray());
        }
        return null;
    }

    @Override
    public String toString() {
        return "[FuncCallExp]:" + funcName.toString() + "(" + params.toString() + ")";
    }
}
