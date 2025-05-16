package org.jeff.jsw.exprs;

import org.jeff.jsw.JsContext;
import org.jeff.jsw.objs.JsObject;
import org.jeff.jsw.objs.JsOperator;

public class IndexAccessExpr implements Assignable
{
    Expression target;
    Expression index;

    public IndexAccessExpr(Expression target, Expression key)
    {
        this.target = target;
        this.index = key;
    }

    @Override
    public JsObject eval(JsContext context)
    {
        JsObject obj = target.eval(context);
        JsObject key = this.index.eval(context);
        return JsOperator.getIndex(obj, key);
    }

    @Override
    public void assign(JsContext context, JsObject value)
    {
        JsObject obj = target.eval(context);
        JsObject key = this.index.eval(context);
        JsOperator.setIndex(obj, key, value);
    }
}
