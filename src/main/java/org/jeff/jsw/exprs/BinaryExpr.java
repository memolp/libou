package org.jeff.jsw.exprs;

import org.jeff.jsw.Env;

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

    public Object eval(Env env) {
        Object l = left.eval(env);
        Object r = right.eval(env);
        switch (op) {
            case "+": return toNumber(l) + toNumber(r);
            case "-": return toNumber(l) - toNumber(r);
            case "*": return toNumber(l) * toNumber(r);
            case "/": return toNumber(l) / toNumber(r);
            case "==": return l.equals(r);
            case "!=": return !l.equals(r);
            case "<": return toNumber(l) < toNumber(r);
            case ">": return toNumber(l) > toNumber(r);
            default: throw new RuntimeException("Unknown operator: " + op);
        }
    }

    private double toNumber(Object val) {
        return ((Number) val).doubleValue();
    }

    @Override
    public String toString()
    {
        return "[BinaryExpr]:" + left.toString() + op + right.toString();
    }
}
