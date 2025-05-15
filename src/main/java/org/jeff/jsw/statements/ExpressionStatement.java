package org.jeff.jsw.statements;

import org.jeff.jsw.Env;
import org.jeff.jsw.exprs.Expression;

public class ExpressionStatement implements Statement
{
    Expression expression;
    public ExpressionStatement(Expression expression)
    {
        this.expression = expression;
    }

    @Override
    public Object execute(Env env, Object...args)
    {
        return expression.eval(env);
    }

    @Override
    public String toString()
    {
        return "[ExpStatement]: " + this.expression.toString();
    }
}
