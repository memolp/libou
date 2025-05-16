package org.jeff.jsw.statements;

import org.jeff.jsw.exprs.*;
import org.jeff.jsw.objs.JsNumber;
import org.jeff.jsw.objs.JsString;
import org.jeff.jsw.tokens.Token;
import org.jeff.jsw.tokens.TokenType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ASTParser
{
    private final List<Token> tokens;
    private int pos = 0;

    public ASTParser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public BlockStatement parseStatements() {

        BlockStatement blockStatement = new BlockStatement();
        while (!match(TokenType.EOF)) {
            blockStatement.addStatement(parseStatement());
        }
        return blockStatement;
    }

    /**
     * 生成语句。合法的语句有：
     * 1. 变量声明语句 let a = 0;
     * 2. 赋值语句 a = 0  a.b = 3
     * 3. 分支语句 if  if-else  if-elif-else
     * 4. 循环语句  for  while
     * 5. 函数声明语句 function x
     * 6. 函数调用语句 x()
     * 7. return 返回， break循环中断， continue 循环继续
     * @return
     */
    private Statement parseStatement()
    {
        if (match(TokenType.LET))
        {
            return this.parseAssignStatement();
        } else if (match(TokenType.IF))
        {
            return this.parseIfStatement();
        } else if(match(TokenType.FOR))
        {
            return this.parseForStatement();
        } else if(match(TokenType.WHERE))
        {
            return this.parseWhileStatement();
        } else if(match(TokenType.FUNCTION))
        {
            return this.parseFunctionStatement();
        } else if(match(TokenType.RETURN))
        {
            return this.parseReturnStatement();
        } else if(match(TokenType.BREAK))
        {
            return null;
        } else if(match(TokenType.CONTINUE))
        {
            return null;
        }
        else
        {
            return this.parseExpStatement();
        }
    }

    private LetStatement parseLetStatement(String name)
    {
        Expression expr = null;
        if(match(TokenType.OP_ASSIGN))  // 有后续
        {
            expr = this.parseExpression();
        }
        match(TokenType.SEMICOLON); // 丢弃末尾的分号`;`
        return new LetStatement(name, expr);
    }
    /**
     * 声明语句
     * let a;
     * let a = 0;
     * let a = function(){}
     * @return
     */
    private LetStatement parseAssignStatement()
    {
        String name = expect(TokenType.IDENT).text;
        Expression expr = null;
        if(match(TokenType.OP_ASSIGN))  // 有后续
        {
            expr = this.parseExpression();
        }
        match(TokenType.SEMICOLON); // 丢弃末尾的分号`;`
        return new LetStatement(name, expr);
    }

    /**
     * 解析分支语句
     * 1. if(){}
     * 2. if(){} else {}
     * 3. if(){} elif(){} else{}
     * @return
     */
    private IfStatement parseIfStatement()
    {
        IfStatement statement = this.parseBranchStatement(true);
        List<IfStatement> branchs = new ArrayList<>();
        while (match(TokenType.ELIF))
        {
            branchs.add(parseBranchStatement(true));
        }
        if(match(TokenType.ELSE))
        {
            branchs.add(parseBranchStatement(false));
        }
        statement.setBranchs(branchs);
        return statement;
    }

    private IfStatement parseBranchStatement(boolean condition)
    {
        Expression cond = null;
        if(condition)
        {
            expect(TokenType.PAREN_OPEN);
            cond = parseExpression();
            expect(TokenType.PAREN_CLOSE);
        }
        expect(TokenType.BRACE_OPEN);
        BlockStatement body = new BlockStatement();
        while (!match(TokenType.BRACE_CLOSE))
        {
            body.addStatement(parseStatement());
        }
        return new IfStatement(cond, body);
    }

    /**
     * for循环
     * 1. for(let i = 0; i < n; i++){} 传统for循环
     * 2. for(let i in b){}  新的迭代器
     * @return
     */
    private Statement parseForStatement()
    {
        expect(TokenType.PAREN_OPEN);
        expect(TokenType.LET);
        String varName = expect(TokenType.IDENT).text;
        if(match(TokenType.IN))
        {
            Expression collection = parseExpression();
            expect(TokenType.PAREN_CLOSE);
            expect(TokenType.BRACE_OPEN);
            BlockStatement body = new BlockStatement();
            while (!match(TokenType.BRACE_CLOSE)) {
                body.addStatement(parseStatement());
            }
            return new ForeachStatement(varName, collection, body);
        }else {
            Statement init = this.parseLetStatement(varName);
            Expression cond = this.parseExpression();
            expect(TokenType.SEMICOLON);
            Statement update = this.parseStatement();
            expect(TokenType.PAREN_CLOSE);
            expect(TokenType.BRACE_OPEN);
            BlockStatement body = new BlockStatement();
            while (!match(TokenType.BRACE_CLOSE)) {
                body.addStatement(parseStatement());
            }
            return new ForStatement(init, cond, update, body);
        }
    }
    /**
     * 传统where循环
     * where(cond){}
     * @return
     */
    private Statement parseWhileStatement()
    {
        expect(TokenType.PAREN_OPEN);
        Expression cond = parseExpression();
        expect(TokenType.PAREN_CLOSE);
        expect(TokenType.BRACE_OPEN);
        BlockStatement body = new BlockStatement();
        while (!match(TokenType.BRACE_CLOSE))
        {
            body.addStatement(parseStatement());
        }
        return new WhereStatement(cond, body);
    }

    /**
     * 函数声明
     * @return
     */
    private Statement parseFunctionStatement()
    {
        String func_name = null;
        if(!match(TokenType.PAREN_OPEN))  //如果不是( 说明有函数名
        {
            func_name = expect(TokenType.IDENT).text;
            expect(TokenType.PAREN_OPEN);
        }
        // 函数参数
        List<String> arguments = new ArrayList<>();
        while (peek().type != TokenType.PAREN_CLOSE)
        {
            String param = expect(TokenType.IDENT).text;
            arguments.add(param);
            if(!match(TokenType.COMMA)) break;
        }
        expect(TokenType.PAREN_CLOSE);
        // 函数体
        expect(TokenType.BRACE_OPEN);
        BlockStatement body = new BlockStatement();
        while (!match(TokenType.BRACE_CLOSE))
        {
            body.addStatement(parseStatement());
        }
        return new FunctionStatement(func_name, arguments, body);
    }

    private Statement parseExpStatement()
    {
        Expression expr = this.parseExpression();
        return new ExpressionStatement(expr);
    }

    private Statement parseReturnStatement()
    {
        Expression expr = null;
        if(!match(TokenType.SEMICOLON))
        {
            expr = this.parseExpression();
        }
        match(TokenType.SEMICOLON);
        return new ReturnStatement(expr);
    }

    /**
     * 解析一个表达式，表达式有多种
     * @return
     */
    private Expression parseExpression()
    {
        return parseExpression(0);
    }

    private Expression parseExpression(int precedence)
    {
        Expression left = parseUnary();
        while (true)
        {
            Token op = peek();
            if(this.isBinaryOp(op.type))
            {
                int opPrec = getPrecedence(op.type);
                if (opPrec < precedence) break;
                next(); // consume operator
                Expression right = parseExpression(opPrec + 1);
                left = new BinaryExpr(left, op.text, right);
            }else if(op.type == TokenType.DOT)
            {
                expect(TokenType.DOT);
                Expression right = this.parsePrimary();
                if(!(right instanceof VarExpr)) throw new RuntimeException("Index name error");
                left = new AttributeExpr(left, new LiteralExpr(new JsString(((VarExpr)right).name)));
            }else if(op.type == TokenType.PAREN_OPEN)  // function call
            {
                left = this.parseCallExpression(left);
            }else if(op.type == TokenType.OP_ASSIGN) // 赋值
            {
                expect(TokenType.OP_ASSIGN);
                Expression right = parseExpression();
                return new AssignExpr((Assignable) left, right);
            }else if(op.type == TokenType.SQUARE_OPEN) // 属性访问
            {
                expect(TokenType.SQUARE_OPEN);
                Expression index = parseExpression();
                expect(TokenType.SQUARE_CLOSE);
                left = new AttributeExpr(left, index);
            }else if(op.type == TokenType.OP_INCR || op.type == TokenType.OP_DECR)
            {
                next();
                left = new UnaryExpr(op.text, left);
            }
            else
            {
                break;
            }
        }
        return left;
    }

    /**
     * 解析一元表达式 目前能出现的一元表达式为 !a  -3 ~4
     * @return
     */
    private Expression parseUnary()
    {
        if (match(TokenType.OP_NOT) || match(TokenType.OP_MINUS) || match(TokenType.OP_BIT_NOR) || match(TokenType.OP_INCR) || match(TokenType.OP_DECR))
        {
            Token operator = prev();
            Expression right = parseUnary();
            return new UnaryExpr(operator.text, right);
        }
        return parsePrimary();
    }

    private Expression parsePrimary()
    {
        Token t = next();
        switch (t.type)
        {
            case NUMBER: return new LiteralExpr(this.parseNumber(t.text));
            case STRING: return new LiteralExpr(new JsString(t.text));
            case IDENT:
                return new VarExpr(t.text);
            case PAREN_OPEN:  // (  )
                Expression e = parseExpression();
                expect(TokenType.PAREN_CLOSE);
                return e;
            case FUNCTION:  // function 函数定义
                return parseFunctionExpression();
            case SQUARE_OPEN:
                return parseListExpression();
            case BRACE_OPEN:
                return parseMapExpression();
            default:
                throw new RuntimeException("Unexpected token in expression: " + t);
        }
    }

    private FunctionExpr parseFunctionExpression()
    {
        expect(TokenType.PAREN_OPEN);
        List<String> params = new ArrayList<>();
        if (peek().type != TokenType.PAREN_CLOSE)
        {
            do {
                params.add(expect(TokenType.IDENT).text);
            } while (match(TokenType.COMMA));
        }
        expect(TokenType.PAREN_CLOSE);
        expect(TokenType.BRACE_OPEN);
        BlockStatement body = new BlockStatement();
        while (!match(TokenType.BRACE_CLOSE)) {
            body.addStatement(parseStatement());
        }
        return new FunctionExpr(params, body);
    }

    private Expression parseListExpression()
    {
        List<Expression> items = new ArrayList<>();
        do {
            if (peek().type == TokenType.SQUARE_CLOSE) break;
            Expression item = this.parseExpression();
            items.add(item);
        }while (match(TokenType.COMMA));
        expect(TokenType.SQUARE_CLOSE);
        return new ListExpr(items);
    }

    private Expression parseMapExpression()
    {
        Map<Expression, Expression> map = new HashMap<>();
        do {
            if(peek().type == TokenType.BRACE_CLOSE) break;
            Expression key = this.parseExpression();
            expect(TokenType.COLON);
            Expression value = this.parseExpression();
            map.put(key, value);
        }while (match(TokenType.COMMA));
        expect(TokenType.BRACE_CLOSE);
        return new MapExpr(map);
    }

    private FunctionCallExpr parseCallExpression(Expression func)
    {
        expect(TokenType.PAREN_OPEN);
        List<Expression> args = new ArrayList<>();
        if (peek().type != TokenType.PAREN_CLOSE)
        {
            do {
                args.add(parseExpression());
            }while(match(TokenType.COMMA));
        }
        expect(TokenType.PAREN_CLOSE);
        match(TokenType.SEMICOLON);
        return new FunctionCallExpr(func, args);
    }

    private JsNumber parseNumber(String s)
    {
        if (s.startsWith("0x") || s.startsWith("0X"))
            return new JsNumber(Integer.parseInt(s.substring(2), 16));
        if (s.startsWith("0o") || s.startsWith("0O"))
            return new JsNumber(Integer.parseInt(s.substring(2), 8));
        if (s.contains(".") || s.contains("e") || s.contains("E"))
            return new JsNumber(Double.parseDouble(s));
        return new JsNumber(Integer.parseInt(s));
    }

    private int getPrecedence(TokenType type)
    {
        switch (type)
        {
            case DOT: return 10;
            case OP_NOT: return 9;
            case OP_STAR: case OP_SLASH: case OP_PERCENT: return 8;
            case OP_PLUS: case OP_MINUS: return 7;
            case OP_INCR: case OP_DECR: return 6;  // 优先级问题
            case OP_LEFT_SHIFT: case OP_RIGHT_SHIFT: return 6;
            case OP_LT: case OP_LTE: case OP_GT: case OP_GTE: return 5;
            case OP_EQ: case OP_NEQ: return 4;
            case OP_BIT_AND: return 3;
            case OP_BIT_OR: return 2;
            case OP_AND: return 1;
            case OP_OR: return 0;
            default:
                return 0;
        }
    }

    private boolean isBinaryOp(TokenType type)
    {
        switch (type)
        {
            case OP_STAR: case OP_SLASH: case OP_PERCENT: return true;
            case OP_PLUS: case OP_MINUS: return true;
            case OP_LEFT_SHIFT: case OP_RIGHT_SHIFT: return true;
            case OP_LT: case OP_LTE: case OP_GT: case OP_GTE: return true;
            case OP_EQ: case OP_NEQ: return true;
            case OP_BIT_AND: return true;
            case OP_BIT_OR: return true;
            case OP_AND: return true;
            case OP_OR: return true;
            default: return false;
        }
    }

    private boolean match(TokenType type) {
        if (peek().type == type) {
            pos++;
            return true;
        }
        return false;
    }

    private Token expect(TokenType type) {
        Token t = next();
        if (t.type != type) {
            throw new RuntimeException("Expected " + type + ", got " + t);
        }
        return t;
    }

    private Token next() {
        return tokens.get(pos++);
    }

    private Token peek() {
        return tokens.get(pos);
    }

    private Token prev() {
        return tokens.get(pos - 1);
    }
}
