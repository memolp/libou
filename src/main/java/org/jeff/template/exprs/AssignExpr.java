package org.jeff.template.exprs;

import org.jeff.template.RenderContext;
import org.jeff.template.tokens.TokenType;

public class AssignExpr implements Expression
{
    private final Assignable left;
    private final Expression right;
    private final TokenType op;
    public AssignExpr(Assignable left, Expression right, TokenType op)
    {
        this.left = left;
        this.right = right;
        this.op = op;
    }

    @Override
    public Object eval(RenderContext context)
    {
        Object value = this.right.eval(context);
        if(op == TokenType.OP_ASSIGN)
        {
            this.left.set(context, value);
            return null;
        }
        Object var = this.left.eval(context);
        switch (op)
        {
            case OP_PLUS_ASSIGN:
                this.left.set(context, ExprOperator.add(var, value));break;
            case OP_MINUS_ASSIGN:
                this.left.set(context, ExprOperator.sub(var, value));break;
            case OP_MULTI_ASSIGN:
                this.left.set(context, ExprOperator.mul(var, value));break;
            case OP_DIV_ASSIGN:
                this.left.set(context, ExprOperator.div(var, value));break;
            case OP_MOD_ASSIGN:
                this.left.set(context, ExprOperator.mod(var, value));break;
            default:
                throw new RuntimeException("Invalid operator");
        }
        return null;
    }
}
