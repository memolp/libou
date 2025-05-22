package org.jeff.jsw.statements;

import org.jeff.jsw.JsContext;
import org.jeff.jsw.exceptions.ContinueException;
import org.jeff.jsw.objs.JsObject;

public class ContinueStatement implements Statement
{
    @Override
    public JsObject execute(JsContext context)
    {
        throw new ContinueException();
    }
}
