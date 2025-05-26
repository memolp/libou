package org.jeff.template.tokens;

public class Token
{
    public String text;
    public TokenType type;
    public int line;
    public int column;

    public Token(TokenType type, String text, int column, int line)
    {
        this.text = text;
        this.type = type;
        this.line = line;
        this.column = column;
    }
}
