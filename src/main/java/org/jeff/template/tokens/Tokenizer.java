package org.jeff.template.tokens;

import java.util.LinkedList;
import java.util.List;

public class Tokenizer
{
    private final String src;
    private final int line;
    private int pos = 0;
    private final int len;
    public final List<Token> tokens = new LinkedList<>();

    public Tokenizer(String src, int line)
    {
        this.src = src;
        this.len = src.length();
        this.line = line;
    }

    private void addTokenAndSkip(TokenType type, String text)
    {
        this.addToken(type, text);
        advance(text.length());
    }

    private void addToken(TokenType type, String text)
    {
        Token token = new Token(type, text, this.pos, this.line);
        this.tokens.add(token);
    }

    private void readOperator(char c)
    {
        switch (c)
        {
            case '+':
                if(peek(1) == '+') {  // ++
                    this.addTokenAndSkip(TokenType.OP_INC, "++"); return;
                }else if(peek(1) == '=') { // +=
                    this.addTokenAndSkip(TokenType.OP_PLUS_ASSIGN, "+="); return;
                }
                this.addTokenAndSkip(TokenType.OP_PLUS, "+"); return;
            case '-':
                if(peek(1) == '-') { // --
                    this.addTokenAndSkip(TokenType.OP_DEC, "--"); return;
                }else if(peek(1) == '=') {// -=
                    this.addTokenAndSkip(TokenType.OP_MINUS_ASSIGN, "-="); return;
                }
                this.addTokenAndSkip(TokenType.OP_MINUS, "-"); return;
            case '*':
                if(peek(1) == '=') { // *=
                    this.addTokenAndSkip(TokenType.OP_MULTI_ASSIGN, "*="); return;
                }
                this.addTokenAndSkip(TokenType.OP_MULTI, "*"); return;
            case '/':
                if(peek(1) == '=') { // /=
                    this.addTokenAndSkip(TokenType.OP_DIV_ASSIGN, "/="); return;
                }else if(peek(1) == '/')
                {
                    this.skipRecommend(); return;
                }
                this.addTokenAndSkip(TokenType.OP_DIV, "/"); return;
            case '%':
                if(peek(1) == '=') { // %=
                    this.addTokenAndSkip(TokenType.OP_MOD_ASSIGN, "%="); return;
                }
                this.addTokenAndSkip(TokenType.OP_MOD, "%"); return;
            case '|':
                if(peek(1) == '|') {
                    this.addTokenAndSkip(TokenType.OP_OR, "||"); return;
                }
                this.addTokenAndSkip(TokenType.OP_BIT_OR, "|"); return;
            case '&':
                if(peek(1) == '&') { // &&
                    this.addTokenAndSkip(TokenType.OP_AND, "&&"); return;
                }
                this.addTokenAndSkip(TokenType.OP_BIT_AND, "&"); return;
            case '~':
                this.addTokenAndSkip(TokenType.OP_BIT_NOT, "~"); return;
            case '=':
                if(peek(1) == '=') { // ==
                    this.addTokenAndSkip(TokenType.OP_EQ, "=="); return;
                }
                this.addTokenAndSkip(TokenType.OP_ASSIGN, "="); return;
            case '.':
                if(Character.isDigit(peek(1))) {
                    this.readNumber(true); return;
                }
                this.addTokenAndSkip(TokenType.DOT, "."); return;
            case '!':
                if (peek(1) == '=') {
                    this.addTokenAndSkip(TokenType.OP_NE, "!="); return;
                }
                this.addTokenAndSkip(TokenType.OP_NOT, "!"); return;
            case '<':
                if(peek(1) == '=') {
                    this.addTokenAndSkip(TokenType.OP_LE, "<="); return;
                }else if(peek(1) == '<') {
                    this.addTokenAndSkip(TokenType.OP_BIT_LEFT, "<<"); return;
                }
                this.addTokenAndSkip(TokenType.OP_LT, "<"); return;
            case '>':
                if(peek(1) == '=') {
                    this.addTokenAndSkip(TokenType.OP_GE, ">="); return;
                }else if(peek(1) == '>') {
                    this.addTokenAndSkip(TokenType.OP_BIT_RIGHT, ">>"); return;
                }
                this.addTokenAndSkip(TokenType.OP_GT, ">"); return;
            case '(': this.addTokenAndSkip(TokenType.LPAREN, "("); return;
            case ')': this.addTokenAndSkip(TokenType.RPAREN, ")"); return;
            case '{': this.addTokenAndSkip(TokenType.LBRACE, "{"); return;
            case '}': this.addTokenAndSkip(TokenType.RBRACE, "}"); return;
            case '[': this.addTokenAndSkip(TokenType.LBRACKET, "["); return;
            case ']': this.addTokenAndSkip(TokenType.RBRACKET, "]"); return;
            case ';': this.addTokenAndSkip(TokenType.SEMICOLON, ";"); return;
            case ',': this.addTokenAndSkip(TokenType.COMMA, ","); return;
            case ':': this.addTokenAndSkip(TokenType.COLON, ":"); return;
            default:
                error("Unexpected character: " + c);
        }
    }

    public void tokenize()
    {
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
                this.readIdentifierOrKeyword();
            } else if (Character.isDigit(c))
            {
                this.readNumber(false);
            } else if (c == '"' || c == '\'')  // 读取字符串
            {
                this.readString(c);
            } else {
                this.readOperator(c);
            }
        }
        this.addTokenAndSkip(TokenType.EOF, "");
    }

    /**
     * 读取变量或者关键字
     */
    private void readIdentifierOrKeyword()
    {
        int start = pos;
        while (pos < len && (Character.isLetterOrDigit(peek()) || peek() == '_')) {
            advance();
        }
        String word = src.substring(start, pos);
        this.addToken(TokenKeyword.get(word, TokenType.IDENTIFIER), word);
    }

    /**
     * 读取数字
     */
    private void readNumber(boolean hasDot)
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
                this.addToken(TokenType.INT, src.substring(start, pos));
                return;
            }else if(next == 'o' || next == 'O')
            {
                advance(2);
                while(pos < len && isOctDigit(peek())) advance();
                this.addToken(TokenType.INT, src.substring(start, pos));
                return;
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
        if(hasDot)
            this.addToken(TokenType.FLOAT, src.substring(start, pos));
        else
            this.addToken(TokenType.INT, src.substring(start, pos));
    }

    private boolean isHexDigit(char c) {
        return Character.isDigit(c) || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F');
    }

    private boolean isOctDigit(char c) {
        return c >= '0' && c <= '7';
    }
    private void skipRecommend()
    {
        while(peek() != '\n') advance();
        advance();
    }
    /**
     * 读取字符串
     * @param tag 字符串是双引号还是单引号
     */
    private void readString(char tag)
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
        this.addToken(TokenType.STRING, sb.toString());
    }

    private char peek() {
        return this.peek(0);
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
