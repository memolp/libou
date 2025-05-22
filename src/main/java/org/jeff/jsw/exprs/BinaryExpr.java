package org.jeff.jsw.exprs;

import org.jeff.jsw.JsContext;
import org.jeff.jsw.objs.JsOperator;
import org.jeff.jsw.objs.JsObject;

public class BinaryExpr implements Expression
{
    private final Expression left;
    private final String op;
    private final Expression right;

    public BinaryExpr(Expression left, String op, Expression right) {
        this.left = left;
        this.op = op;
        this.right = right;
    }

    public JsObject eval(JsContext context)
    {
        JsObject l = left.eval(context);
        JsObject r = right.eval(context);
        switch (op)
        {
            case "+": return JsOperator.add(l, r);
            case "-": return JsOperator.sub(l, r);
            case "*": return JsOperator.mul(l, r);
            case "/": return JsOperator.div(l, r);
            case "%": return JsOperator.mod(l, r);
            case "==": return JsOperator.eq(l, r);
            case "!=": return JsOperator.neq(l, r);
            case "<": return JsOperator.lt(l, r);
            case "<=": return JsOperator.lte(l, r);
            case ">": return JsOperator.gt(l, r);
            case ">=": return JsOperator.gte(l, r);
            case "&&": return JsOperator.and(l, r);
            case "||": return JsOperator.or(l, r);
            default: throw new RuntimeException("Unknown operator: " + op);
        }
    }

    @Override
    public String toString()
    {
        return "[BinaryExpr]:" + left.toString() + op + right.toString();
    }
}
