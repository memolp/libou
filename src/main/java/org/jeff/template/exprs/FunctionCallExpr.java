package org.jeff.template.exprs;

import org.jeff.template.RenderContext;

import java.util.ArrayList;
import java.util.List;

public class FunctionCallExpr implements Expression
{
    private final Expression func;
    private final List<Expression> args;
    public FunctionCallExpr(Expression func, List<Expression> args)
    {
        this.func = func;
        this.args = args;
    }

    @Override
    public Object eval(RenderContext context)
    {
        Object f = func.eval(context);
        if(f instanceof Callable)
        {
            List<Object> a = new ArrayList<>();
            for(Expression e : args)
                a.add(e.eval(context));
            return ((Callable)f).call(context, a.toArray());
        }
        throw new RuntimeException("un callable");
    }
}
