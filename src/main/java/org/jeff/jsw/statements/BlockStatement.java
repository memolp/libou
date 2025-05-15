package org.jeff.jsw.statements;

import org.jeff.jsw.Env;

import java.util.ArrayList;
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
    public Object execute(Env env, Object... args)
    {
        Object result = null;
        for(Statement statement : statements)
        {
            result = statement.execute(env, args);
        }
        return result;
    }

    @Override
    public String toString()
    {
        return "[BlockStatement]" + this.statements.toString();
    }


}
