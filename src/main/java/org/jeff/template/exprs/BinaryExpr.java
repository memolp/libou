package org.jeff.template.exprs;

import org.jeff.template.RenderContext;
import org.jeff.template.tokens.TokenType;

public class BinaryExpr implements Expression
{
    private final Expression left;
    private final Expression right;
    private final TokenType op;
    public BinaryExpr(Expression left, Expression right, TokenType op)
    {
        this.left = left;
        this.op = op;
        this.right = right;
    }

    @Override
    public Object eval(RenderContext context)
    {
        Object v1 = this.left.eval(context);
        Object v2 = this.right.eval(context);
        switch (op)
        {
            case OP_PLUS:
                return ExprOperator.add(v1, v2);
            case OP_MINUS:
                return ExprOperator.sub(v1, v2);
            case OP_MULTI:
                return ExprOperator.mul(v1, v2);
            case OP_DIV:
                return ExprOperator.div(v1, v2);
            case OP_MOD:
                return ExprOperator.mod(v1, v2);
            case OP_LT:
                return ExprOperator.lt(v1, v2);
            case OP_LE:
                return ExprOperator.lte(v1, v2);
            case OP_GT:
                return ExprOperator.gt(v1, v2);
            case OP_GE:
                return ExprOperator.gte(v1, v2);
            case OP_EQ:
                return ExprOperator.eq(v1, v2);
            case OP_NE:
                return ExprOperator.neq(v1, v2);
            case OP_AND:
                return ExprOperator.and(v1, v2);
            case OP_OR:
                return ExprOperator.or(v1, v2);
            case OP_BIT_AND:
            case OP_BIT_OR:
            case OP_BIT_XOR:
            case OP_BIT_LEFT:
            case OP_BIT_RIGHT:
            default:
                throw new RuntimeException("Invalid operator");
        }
    }
}
