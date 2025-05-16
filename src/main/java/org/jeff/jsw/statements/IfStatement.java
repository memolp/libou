package org.jeff.jsw.statements;

import org.jeff.jsw.JsContext;
import org.jeff.jsw.objs.JsOperator;
import org.jeff.jsw.exprs.Expression;
import org.jeff.jsw.objs.JsObject;

import java.util.List;

public class IfStatement implements Statement
{
    private final Expression condition;
    private final BlockStatement body;
    private List<IfStatement> branchs = null;

    public IfStatement(Expression condition, BlockStatement body) {
        this.condition = condition;
        this.body = body;

    }
    public void setBranchs( List<IfStatement> branchs)
    {
        this.branchs = branchs;
    }

    private boolean checkCondition(JsContext jsContext)
    {
        if(condition == null) return true;
        JsObject result = condition.eval(jsContext);
        return JsOperator.toBool(result);
    }

    public JsObject execute(JsContext jsContext)
    {
        if(checkCondition(jsContext))
        {
            return this.body.execute(jsContext);
        }else
        {
            for(IfStatement stmt : branchs)
            {
                if(stmt.checkCondition(jsContext))
                {
                    stmt.execute(jsContext);
                    break;
                }
            }
        }
        return null;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("[IfStatement]: \n");
        if(condition != null)
        {
            sb.append("\t(" + condition.toString() + ") \n");
        }
        sb.append("\t{" + body.toString() + "}");
        if(branchs != null)
        {
            for(Statement stmt : branchs) {
                sb.append("\n");
                sb.append(stmt.toString());
            }
        }
        return sb.toString();
    }
}
