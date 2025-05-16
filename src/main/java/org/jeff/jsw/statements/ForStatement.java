package org.jeff.jsw.statements;

import org.jeff.jsw.JsContext;
import org.jeff.jsw.objs.JsOperator;
import org.jeff.jsw.exceptions.BreakException;
import org.jeff.jsw.exceptions.ContinueException;
import org.jeff.jsw.exceptions.ReturnException;
import org.jeff.jsw.exprs.Expression;
import org.jeff.jsw.objs.JsObject;

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
    public JsObject execute(JsContext jsContext)
    {
        init.execute(jsContext);
        do
        {
            JsObject c = cond.eval(jsContext);
            if(!JsOperator.toBool(c)) break;
            try {
                this.body.execute(jsContext);
            }catch (BreakException e) {break;}
            catch (ContinueException e){}
            catch (ReturnException e){return e.value;}
        }while (true);
        return null;
    }
}
