package org.jeff.jsw.statements;

import org.jeff.jsw.Env;
import org.jeff.jsw.exprs.Expression;

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

    private boolean checkCondition(Env env)
    {
        if(condition == null) return true;
        Object result = condition.eval(env);
        return Boolean.TRUE.equals(result);
    }

    public Object execute(Env env, Object... args)
    {
        if(checkCondition(env))
        {
            return this.body.execute(env, args);
        }else
        {
            for(IfStatement stmt : branchs)
            {
                if(stmt.checkCondition(env))
                {
                    stmt.execute(env, args);
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
