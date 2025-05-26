package org.jeff.template.exprs;

import org.jeff.template.RenderContext;
import org.jeff.template.tokens.TokenType;

public class UnaryExpr implements Expression
{
    private final TokenType op;
    private final Expression expr;
    public UnaryExpr(TokenType op, Expression expr)
    {
        this.op = op;
        this.expr = expr;
    }

    @Override
    public Object eval(RenderContext context)
    {
        Object v = this.expr.eval(context);
        switch (this.op)
        {
            case OP_NOT:
                return ExprOperator.not(v);
            case OP_MINUS:
                return ExprOperator.neg(v);
            case OP_INC:  // TODO 前置和后置
                ((Assignable)expr).set(context, ExprOperator.incr(v));
                return v;
            case OP_DEC:
                ((Assignable)expr).set(context, ExprOperator.decr(v));
                return v;
            case OP_BIT_NOT:
            default:
                throw new RuntimeException("Invalid operator");
        }
    }
}
