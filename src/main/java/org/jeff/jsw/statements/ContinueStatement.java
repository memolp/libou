package org.jeff.jsw.statements;

import org.jeff.jsw.Env;
import org.jeff.jsw.exceptions.ContinueException;

public class ContinueStatement implements Statement
{
    @Override
    public Object execute(Env env, Object... args)
    {
        throw new ContinueException();
    }
}
