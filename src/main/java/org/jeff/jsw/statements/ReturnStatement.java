package org.jeff.jsw.statements;

import org.jeff.jsw.JsContext;
import org.jeff.jsw.exceptions.ReturnException;
import org.jeff.jsw.exprs.Expression;
import org.jeff.jsw.objs.JsNull;
import org.jeff.jsw.objs.JsObject;

public class ReturnStatement implements Statement
{
    Expression expression;
    public ReturnStatement(Expression expression)
    {
        this.expression = expression;
    }
    @Override
    public JsObject execute(JsContext context)
    {
        if(expression != null)
            throw new ReturnException(expression.eval(context));
        throw new ReturnException(JsNull.NIL);
    }
}
