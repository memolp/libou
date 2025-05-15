package org.jeff.jsw.exprs;

import org.jeff.jsw.Env;

public class UnaryExpr implements Expression
{
    final String op;
    final Expression expr;
    public UnaryExpr(String op, Expression expr)
    {
        this.op = op;
        this.expr = expr;
    }
    @Override
    public Object eval(Env env)
    {
        Object value = this.expr.eval(env);
        if(this.op.equals("-"))
        {
            Number v = Number.class.cast(value);
            if(value instanceof Integer) return -v.intValue();
            else if(value instanceof Double) return -v.doubleValue();
        }else if(this.op.equals("!"))
        {
            return !(Boolean)value;
        }else if(this.op.equals("~"))
        {
            Number v = Number.class.cast(value);
            if(value instanceof Integer) return ~v.intValue();
        }
        return null;
    }

    @Override
    public String toString() {
        return "[UnaryExpr]:" + this.op + expr.toString();
    }
}
