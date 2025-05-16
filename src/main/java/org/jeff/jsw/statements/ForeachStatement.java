package org.jeff.jsw.statements;

import org.jeff.jsw.JsContext;
import org.jeff.jsw.exceptions.BreakException;
import org.jeff.jsw.exceptions.ContinueException;
import org.jeff.jsw.exceptions.ReturnException;
import org.jeff.jsw.exprs.Expression;
import org.jeff.jsw.objs.JsNull;
import org.jeff.jsw.objs.JsObject;

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
    public JsObject execute(JsContext jsContext)
    {
        JsContext local = new JsContext(jsContext);
        JsObject v = iter.eval(jsContext);
        if(!(v instanceof Iterable))
        {
            throw new RuntimeException("Not iterable");
        }
        for(Object o : (Iterable)v)
        {
            local.set(varName, o);
            try
            {
                body.execute(local);
            }catch (BreakException e) {break;}
            catch (ContinueException e){}
            catch (ReturnException e){ return e.value;}
        };
        return JsNull.NIL;
    }

    @Override
    public String toString()
    {
        return "[ForeachStatement]: " + varName + " in " + iter.toString() + "\n{\n" + body.toString() + "\n}";
    }
}
