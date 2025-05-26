package org.jeff.template.exprs;

import org.jeff.template.tokens.Token;
import org.jeff.template.tokens.TokenType;
import org.jeff.template.tokens.Tokenizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpressionParser
{
    private Tokenizer tokenizer;
    private int pos = 0;

    public ExpressionParser(String code, int line)
    {
        this.tokenizer = new Tokenizer(code, line);
    }

    public Expression parse()
    {
        this.tokenizer.tokenize();
        return this.parseExpression();
    }

    private Expression parseExpression() {
        return parseExpression(0);
    }

    private Expression parseExpression(int precedence)
    {
        Expression left = parseUnary();
        while (true)
        {
            Token op = this.peek();
            if(this.isBinaryOp(op.type))
            {
                int pre = this.getPrecedence(op.type);
                if(pre < precedence) break;
                this.next();
                Expression right = parseExpression(pre + 1);
                left = new BinaryExpr(left, right, op.type);
            }else if(this.isAssignOp(op.type)) {
                this.next();
                Expression right = parseExpression(precedence);
                return new AssignExpr((Assignable) left, right, op.type);
            }else if(op.type == TokenType.DOT)  // 属性访问
            {
                this.next();
                Expression right = parsePrimary();
                left = new AttributeExpr(left, right);
            }else if(op.type == TokenType.LPAREN) // 函数访问
            {
                left = this.parseCallExpression(left);
            }else if(op.type == TokenType.LBRACKET)  // 字典式的下标访问
            {
                this.next();
                Expression index = parseExpression();
                this.expect(TokenType.RBRACKET);
                left = new AttributeExpr(left, index);
            }else if(op.type == TokenType.OP_INC || op.type == TokenType.OP_DEC) // 后缀 ++ --
            {
                this.next();
                left = new UnaryExpr(op.type, left);
            }else
            {
                break;
            }
        }
        return left;
    }

    private Expression parseCallExpression(Expression func)
    {
        this.expect(TokenType.LPAREN);
        List<Expression> args = new ArrayList<>();
        if(peek().type != TokenType.RPAREN)
        {
            do{
                args.add(parseExpression());
            }while (match(TokenType.COMMA));
        }
        this.expect(TokenType.RPAREN);
        return new FunctionCallExpr(func, args);
    }

    private Expression parseMapExpression()
    {
        Map<Expression, Expression> map = new HashMap<>();
        do{
            if(peek().type == TokenType.RBRACE) break;
            Expression key = this.parseExpression();
            this.expect(TokenType.COLON);
            Expression value = this.parseExpression();
            map.put(key, value);
        }while (match(TokenType.COMMA));
        this.expect(TokenType.RBRACE);
        return new MapExpr(map);
    }

    private Expression parseListExpression()
    {
        List<Expression> items = new ArrayList<>();
        do{
            if(peek().type == TokenType.RBRACKET) break;
            Expression item = this.parseExpression();
            items.add(item);
        }while (this.match(TokenType.COMMA));
        this.expect(TokenType.RBRACKET);
        return new ListExpr(items);
    }

    private Expression parseUnary()
    {
        if(this.match(TokenType.OP_NOT, TokenType.OP_MINUS, TokenType.OP_BIT_NOT, TokenType.OP_INC, TokenType.OP_DEC))
        {
            Token opr = this.prev();
            Expression right = parseUnary();
            return new UnaryExpr(opr.type, right);
        }
        return parsePrimary();
    }

    private Expression parsePrimary()
    {
        Token t = this.next();
        switch (t.type)
        {
            case INT:
                return new LiteralExpr(this.parseInt(t.text));
            case FLOAT:
                return new LiteralExpr(this.parseFloat(t.text));
            case STRING:
                return new LiteralExpr(t.text);
            case IDENTIFIER:
                return new VariableExpr(t.text);
            case LPAREN:    // (
                Expression e = parseExpression();
                this.expect(TokenType.RPAREN);
                return e;
            case LBRACE:  // {
                return this.parseMapExpression();
            case LBRACKET:  // [
                return this.parseListExpression();
            case KEY_BREAK:
                return new BreakExpr();
            case KEY_CONTINUE:
                return new ContinueExpr();
            default:
                throw new RuntimeException("unexpected token " + t.type + " at line " + t.line + ":" + t.column);
        }
    }
    /** 整数 */
    private Object parseInt(String s)
    {
        if (s.startsWith("0x") || s.startsWith("0X"))
            return Integer.parseInt(s.substring(2), 16);
        if (s.startsWith("0o") || s.startsWith("0O"))
            return Integer.parseInt(s.substring(2), 8);
        return Integer.parseInt(s);
    }
    /** 小数 */
    private Object parseFloat(String s)
    {
        return Float.parseFloat(s);
    }

    private int getPrecedence(TokenType type)
    {
        switch (type)
        {
            case DOT: return 10;
            case OP_NOT: return 9;
            case OP_MULTI: case OP_DIV: case OP_MOD: return 8;
            case OP_PLUS: case OP_MINUS: return 7;
            case OP_INC: case OP_DEC: return 6;  // 优先级问题
            case OP_BIT_LEFT: case OP_BIT_RIGHT: return 6;
            case OP_LT: case OP_LE: case OP_GT: case OP_GE: return 5;
            case OP_EQ: case OP_NE: return 4;
            case OP_BIT_AND: return 3;
            case OP_BIT_OR: return 2;
            case OP_AND: return 1;
            case OP_OR:
            default:
                return 0;
        }
    }

    private boolean isBinaryOp(TokenType type)
    {
        switch (type)
        {
            // + - * / %
            case OP_PLUS: case OP_MINUS:
            case OP_MULTI: case OP_DIV: case OP_MOD:
                return true;
            // == != < <= > >=
            case OP_LT: case OP_LE: case OP_GT: case OP_GE:
            case OP_EQ: case OP_NE:
                return true;
            // & | ^
            case OP_BIT_AND: case OP_BIT_OR: case OP_BIT_XOR:
                return true;
            // << >>
            case OP_BIT_LEFT: case OP_BIT_RIGHT:
                return true;
            // && ||
            case OP_AND: case OP_OR:
                return true;
            default: return false;
        }
    }

    private boolean isAssignOp(TokenType type)
    {
        switch (type)
        {
            case OP_ASSIGN:
            case OP_PLUS_ASSIGN:
            case OP_MINUS_ASSIGN:
            case OP_MULTI_ASSIGN:
            case OP_DIV_ASSIGN:
            case OP_MOD_ASSIGN:
                return true;
            default:
                return false;
        }
    }

    private boolean match(TokenType...types)
    {
        Token token = this.peek();
        for(TokenType type : types)
        {
            if(token.type == type)
            {
                this.pos ++;
                return true;
            }
        }
        return false;
    }

    private void expect(TokenType type)
    {
        Token token = this.next();
        if(token.type != type)
            throw new RuntimeException("unexpected token " + token.type + " at line " + token.line + ":" + token.column);
    }

    private Token next()
    {
        return this.tokenizer.tokens.get(this.pos++);
    }

    private Token peek()
    {
        return this.tokenizer.tokens.get(this.pos);
    }

    private Token prev()
    {
        return this.tokenizer.tokens.get(this.pos - 1);
    }
}
