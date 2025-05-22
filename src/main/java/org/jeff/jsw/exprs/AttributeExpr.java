package org.jeff.jsw.exprs;

import org.jeff.jsw.JsContext;
import org.jeff.jsw.objs.JsObject;
import org.jeff.jsw.objs.JsOperator;

public class AttributeExpr implements Assignable
{
    public Expression left;
    public Expression right;
    public AttributeExpr(Expression l, Expression r)
    {
        this.left = l;
        this.right = r;
    }

    @Override
    public JsObject eval(JsContext context)
    {
        JsObject target = this.left.eval(context);
        JsObject index = this.right.eval(context);
        return JsOperator.getIndex(target, index);
    }

    @Override
    public void assign(JsContext context, JsObject value)
    {
        JsObject target = this.left.eval(context);
        JsObject index = this.right.eval(context);
        JsOperator.setIndex(target, index, value);
    }
}
