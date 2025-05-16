package org.jeff.jsw;

import org.jeff.jsw.objs.JsObject;
import org.jeff.jsw.statements.Statement;

public class JsInterpreter
{
    private final JsContext _global;
    public JsInterpreter(JsContext global)
    {
        this._global = global;
    }

    public JsObject executeProgram(Statement statement)
    {
        return statement.execute(this._global);
    }
}
