package org.jeff.jsw.statements;

import org.jeff.jsw.Env;
import org.jeff.jsw.exceptions.BreakException;
import org.jeff.jsw.exceptions.ContinueException;
import org.jeff.jsw.exceptions.ReturnException;
import org.jeff.jsw.exprs.Expression;

import java.util.List;

public class WhereStatement implements Statement
{
    Expression cond;
    BlockStatement body;

    public WhereStatement(Expression cond, BlockStatement body)
    {
        this.cond = cond;
        this.body = body;
    }
    @Override
    public Object execute(Env env, Object...args)
    {
        do {
            Object c = this.cond.eval(env);
            if(!Boolean.TRUE.equals(c)) break;
            try {
                body.execute(env, args);
            }catch (BreakException e) {break;}
            catch (ContinueException e){}
            catch (ReturnException e){return e.value;}
        }while (true);
        return null;
    }
}
