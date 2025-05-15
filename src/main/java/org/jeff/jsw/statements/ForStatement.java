package org.jeff.jsw.statements;

import org.jeff.jsw.Env;
import org.jeff.jsw.exceptions.BreakException;
import org.jeff.jsw.exceptions.ContinueException;
import org.jeff.jsw.exceptions.ReturnException;
import org.jeff.jsw.exprs.Expression;

import java.util.List;

public class ForStatement implements Statement
{

    Statement init;
    Expression cond;
    Statement update;
    BlockStatement body;

    public ForStatement(Statement init, Expression cond, Statement update, BlockStatement body)
    {
        this.init = init;
        this.cond = cond;
        this.update = update;
        this.body = body;
    }

    @Override
    public Object execute(Env env, Object...args)
    {
        init.execute(env, args);
        while (Boolean.TRUE.equals(cond.eval(env)))
        {
            try {
                this.body.execute(env, args);
            }catch (BreakException e) {break;}
            catch (ContinueException e){}
            catch (ReturnException e){return e.value;}
        }
        return null;
    }
}
