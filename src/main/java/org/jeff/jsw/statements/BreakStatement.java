package org.jeff.jsw.statements;

import org.jeff.jsw.JsContext;
import org.jeff.jsw.exceptions.BreakException;
import org.jeff.jsw.objs.JsObject;

public class BreakStatement implements Statement
{
    @Override
    public JsObject execute(JsContext context)
    {
        throw new BreakException();
    }
}
