package org.jeff.template.exprs;

import org.jeff.template.RenderContext;

import java.util.List;

public class AttributeExpr implements Assignable
{
    private final Expression left;
    private final Expression right;
    public AttributeExpr(Expression left, Expression right)
    {
        this.left = left;
        this.right = right;
    }

    @Override
    public Object eval(RenderContext context)
    {
        Object var = this.left.eval(context);
        Object index = this.right.eval(context);
        return ExprOperator.getIndex(var, index);
    }

    @Override
    public void set(RenderContext context, Object value)
    {
        Object var = this.left.eval(context);
        Object index = this.right.eval(context);
        ExprOperator.setIndex(var, index, value);
    }
}
