package org.jeff.jsw.statements;

import org.jeff.jsw.Env;
import org.jeff.jsw.exceptions.ReturnException;
import org.jeff.jsw.exprs.Expression;

public class ReturnStatement implements Statement
{
    Expression expression;
    public ReturnStatement(Expression expression)
    {
        this.expression = expression;
    }
    @Override
    public Object execute(Env env, Object ...args)
    {
        Object val = null;
        if(expression != null) val = expression.eval(env);
        throw new ReturnException(val);
    }
}
