package org.jeff.jsw.statements;

import org.jeff.jsw.JsContext;
import org.jeff.jsw.exprs.Expression;
import org.jeff.jsw.objs.JsObject;

public class ExpressionStatement implements Statement
{
    Expression expression;
    public ExpressionStatement(Expression expression)
    {
        this.expression = expression;
    }

    @Override
    public JsObject execute(JsContext context)
    {
        return expression.eval(context);
    }

    @Override
    public String toString()
    {
        return "[ExpStatement]: " + this.expression.toString();
    }
}
