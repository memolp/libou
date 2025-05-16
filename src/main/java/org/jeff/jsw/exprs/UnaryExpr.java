package org.jeff.jsw.exprs;

import org.jeff.jsw.JsContext;
import org.jeff.jsw.objs.JsOperator;
import org.jeff.jsw.objs.JsObject;

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
    public JsObject eval(JsContext jsContext)
    {
        JsObject value = this.expr.eval(jsContext);
        switch (this.op)
        {
            case "-": return JsOperator.neg(value);
            case "!": return JsOperator.not(value);
            case "~": return JsOperator.xor(value);
            case "++": return JsOperator.incr(value);
            case "--": return JsOperator.decr(value);
            default: return null;
        }
    }

    @Override
    public String toString() {
        return "[UnaryExpr]:" + this.op + expr.toString();
    }
}
