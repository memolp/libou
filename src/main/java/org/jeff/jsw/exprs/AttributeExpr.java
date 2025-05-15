package org.jeff.jsw.exprs;

import org.jeff.jsw.Env;

public class AttributeExpr implements Expression
{
    public Expression left;
    public Expression right;
    public AttributeExpr(Expression l, Expression r)
    {
        this.left = l;
        this.right = r;
    }

    @Override
    public Object eval(Env env)
    {
        // a.b.c.d.e
        Object value = this.left.eval(env);
        return null;
    }
}
