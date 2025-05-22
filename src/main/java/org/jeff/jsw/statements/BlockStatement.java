package org.jeff.jsw.statements;

import org.jeff.jsw.JsContext;
import org.jeff.jsw.exceptions.ReturnException;
import org.jeff.jsw.objs.JsNull;
import org.jeff.jsw.objs.JsObject;

import java.util.LinkedList;
import java.util.List;

public class BlockStatement implements Statement
{
    List<Statement> statements = new LinkedList<>();
    public BlockStatement()
    {

    }

    public void addStatement(Statement statement)
    {
        this.statements.add(statement);
    }

    @Override
    public JsObject execute(JsContext context)
    {
        try
        {
            JsObject value = JsNull.NIL;
            for (Statement statement : statements) {
                value = statement.execute(context);
            }
            return value;
        }catch (ReturnException e)
        {
            return e.value;
        }
    }

    @Override
    public String toString()
    {
        return "[BlockStatement]" + this.statements.toString();
    }


}
