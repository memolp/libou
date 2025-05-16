package org.jeff.jsw.statements;

import org.jeff.jsw.JsContext;
import org.jeff.jsw.objs.JsOperator;
import org.jeff.jsw.exceptions.BreakException;
import org.jeff.jsw.exceptions.ContinueException;
import org.jeff.jsw.exceptions.ReturnException;
import org.jeff.jsw.exprs.Expression;
import org.jeff.jsw.objs.JsObject;

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
    public JsObject execute(JsContext jsContext)
    {
        do {
            JsObject c = this.cond.eval(jsContext);
            if(!JsOperator.toBool(c)) break;
            try {
                body.execute(jsContext);
            }catch (BreakException e) {break;}
            catch (ContinueException e){}
            catch (ReturnException e){return e.value;}
        }while (true);
        return null;
    }
}
