package org.jeff.jsw.exprs;

import org.jeff.jsw.JsContext;
import org.jeff.jsw.objs.JsObject;

public class AssignExpr implements Expression
{
    Assignable target;
    Expression right;

    public AssignExpr(Assignable t, Expression r)
    {
        this.target = t;
        this.right = r;
    }

    @Override
    public JsObject eval(JsContext context)
    {
        this.target.assign(context, right.eval(context));
        return null;
    }
}
