package org.jeff.jsw.tokens;

import java.util.ArrayList;
import java.util.List;

/**
 * 词法解析器
 */
public class Tokenizer
{
    private final String src;
    private int pos = 0;
    private final int len;

    public Tokenizer(String src)
    {
        this.src = src;
        this.len = src.length();
    }

    public List<Token> tokenize()
    {
        List<Token> tokens = new ArrayList<>();
        while (pos < len)
        {
            char c = peek();
            if (Character.isWhitespace(c))
            {
                advance();
                continue;
            }
            if (Character.isLetter(c) || c == '_')
            {
                tokens.add(readIdentifierOrKeyword());
            } else if (Character.isDigit(c))
            {
                tokens.add(readNumber(false));
            } else if (c == '"' || c == '\'')  // 读取字符串
            {
                tokens.add(readString(c));
            } else {
                switch (c)
                {
                    case '+':
                        tokens.add(new Token(TokenType.OP_PLUS, "+")); advance(); break;
                    case '-':
                        tokens.add(new Token(TokenType.OP_MINUS, "-")); advance(); break;
                    case '*':
                        tokens.add(new Token(TokenType.OP_STAR, "*")); advance(); break;
                    case '/':
                        tokens.add(new Token(TokenType.OP_SLASH, "/")); advance(); break;
                    case '%':
                        tokens.add(new Token(TokenType.OP_PERCENT, "|")); advance(); break;
                    case '|':
                        if(peek(1) == '|')
                        {
                            tokens.add(new Token(TokenType.OP_OR, "||"));
                            advance(2);
                        }else
                        {
                            tokens.add(new Token(TokenType.OP_BIT_OR, "|"));
                            advance();
                        }
                        break;
                    case '&':
                        if(peek(1) == '&')
                        {
                            tokens.add(new Token(TokenType.OP_AND, "&&"));
                            advance(2);
                        }else
                        {
                            tokens.add(new Token(TokenType.OP_BIT_AND, "&"));
                            advance();
                        }
                        break;
                    case '~':
                        tokens.add(new Token(TokenType.OP_BIT_NOR, "~")); advance(); break;
                    case '=':
                        if (peek(1) == '=') {
                            tokens.add(new Token(TokenType.OP_EQ, "=="));
                            advance(2);
                        } else {
                            tokens.add(new Token(TokenType.OP_ASSIGN, "="));
                            advance();
                        }
                        break;
                    case '.':
                        if(Character.isDigit(peek(1)))
                        {
                            tokens.add(readNumber(true));
                        }else
                        {
                            tokens.add(new Token(TokenType.DOT, "."));
                            advance();
                        }
                        break;
                    case '!':
                        if (peek(1) == '=') {
                            tokens.add(new Token(TokenType.OP_NEQ, "!="));
                            advance(2);
                        } else {
                            tokens.add(new Token(TokenType.OP_NOT, "!"));
                            advance();
                        }
                        break;
                    case '<':
                        if(peek(1) == '=')
                        {
                            tokens.add(new Token(TokenType.OP_LTE, "<=")); advance(2);
                        }else if(peek(1) == '<')
                        {
                            tokens.add(new Token(TokenType.OP_LEFT_SHIFT, "<<")); advance(2);
                        }
                        else
                        {
                            tokens.add(new Token(TokenType.OP_LT, "<")); advance(1);
                        }
                        break;
                    case '>':
                        if(peek(1) == '=') {
                            tokens.add(new Token(TokenType.OP_GTE, ">=")); advance(2);
                        }else if(peek(1) == '>') {
                            tokens.add(new Token(TokenType.OP_RIGHT_SHIFT, ">>")); advance(2);
                        }
                        else {
                            tokens.add(new Token(TokenType.OP_GT, ">")); advance();
                        }
                        break;
                    case '(': tokens.add(new Token(TokenType.PAREN_OPEN, "(")); advance(); break;
                    case ')': tokens.add(new Token(TokenType.PAREN_CLOSE, ")")); advance(); break;
                    case '{': tokens.add(new Token(TokenType.BRACE_OPEN, "{")); advance(); break;
                    case '}': tokens.add(new Token(TokenType.BRACE_CLOSE, "}")); advance(); break;
                    case ';': tokens.add(new Token(TokenType.SEMICOLON, ";")); advance(); break;
                    case ',': tokens.add(new Token(TokenType.COMMA, ",")); advance(); break;
                    default:
                        error("Unexpected character: " + c);
                }
            }
        }
        tokens.add(new Token(TokenType.EOF, ""));
        return tokens;
    }

    /**
     * 读取变量或者关键字
     * @return Token
     */
    private Token readIdentifierOrKeyword()
    {
        int start = pos;
        while (pos < len && (Character.isLetterOrDigit(peek()) || peek() == '_')) {
            advance();
        }
        String word = src.substring(start, pos);
        return new Token(TokenKeyword.get(word, TokenType.IDENT), word);
    }

    /**
     * 读取数字
     * @return
     */
    private Token readNumber(boolean hasDot)
    {
        int start = pos;
        char c = peek();
        if(c == '0')  // 针对第一个为0的情况特殊处理
        {
            char next = peek(1);
            if(next == 'x' || next == 'X')
            {
                advance(2);
                while(pos < len && isHexDigit(peek())) advance();
                return new Token(TokenType.NUMBER, src.substring(start, pos));
            }else if(next == 'o' || next == 'O')
            {
                advance(2);
                while(pos < len && isOctDigit(peek())) advance();
                return new Token(TokenType.NUMBER, src.substring(start, pos));
            }
        }
        while (pos < len )
        {
            c = peek();
            if(Character.isDigit(c))
                advance();
            else if(c == '.' && !hasDot)
            {
                hasDot = true;
                advance();
            }else
            {
                break;
            }
        }
        c = peek();
        if(c == 'e' || c == 'E')
        {
            char next = peek(1);
            if(Character.isDigit(next) || next == '+' || next == '-')
            {
                advance();
                if(!Character.isDigit(next)) advance();
                while (pos < len && Character.isDigit(peek())) advance();
            }
        }
        return new Token(TokenType.NUMBER, src.substring(start, pos));
    }

    private boolean isHexDigit(char c) {
        return Character.isDigit(c) || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F');
    }

    private boolean isOctDigit(char c) {
        return c >= '0' && c <= '7';
    }

    /**
     * 读取字符串
     * @param tag 字符串是双引号还是单引号
     * @return 字符串Token
     */
    private Token readString(char tag)
    {
        StringBuilder sb = new StringBuilder();
        advance(); // skip opening "
        while (pos < len) {
            char c = peek();
            if (c == tag)
            {
                advance(); // skip closing "
                break;
            }
            if (c == '\\')
            {
                advance();
                char esc = peek();
                switch (esc)
                {
                    case 'n':  sb.append('\n'); break;
                    case 't':  sb.append('\t'); break;
                    case '"':  sb.append('"'); break;
                    case '\'': sb.append('\''); break;
                    case '\\': sb.append('\\'); break;
                    default: error("Unknown escape sequence: \\" + esc);
                }
                advance();
            } else {
                sb.append(c);
                advance();
            }
        }
        return new Token(TokenType.STRING, sb.toString());
    }

    private char peek() {
        return src.charAt(pos);
    }

    private char peek(int offset) {
        if (pos + offset >= len) return '\0';
        return src.charAt(pos + offset);
    }

    private void advance() {
        pos++;
    }

    private void advance(int n) {
        pos += n;
    }

    private void error(String msg) {
        throw new RuntimeException("Tokenizer error at pos " + pos + ": " + msg);
    }
}
