package org.jeff.jsw.statements;

import org.jeff.jsw.Env;
import org.jeff.jsw.exceptions.BreakException;
import org.jeff.jsw.exceptions.ContinueException;
import org.jeff.jsw.exceptions.ReturnException;
import org.jeff.jsw.exprs.Expression;

import java.util.List;

public class ForeachStatement implements Statement
{
    String varName;
    Expression iter;
    BlockStatement body;

    public ForeachStatement(String varName, Expression iter, BlockStatement body)
    {
        this.varName = varName;
        this.iter = iter;
        this.body = body;
    }

    @Override
    public Object execute(Env env, Object... args)
    {
        Env local = new Env(env);
        Object result = null;
        Object v = iter.eval(env);
        Iterable vitr = Iterable.class.cast(v);
        for(Object o : vitr)
        {
            local.set(varName, o);
            try
            {
                result = body.execute(local, args);
            }catch (BreakException e) {break;}
            catch (ContinueException e){}
            catch (ReturnException e){ return e.value;}
        };
        return result;
    }

    @Override
    public String toString()
    {
        return "[ForeachStatement]: " + varName + " in " + iter.toString() + "\n{\n" + body.toString() + "\n}";
    }
}
