package org.jeff.jsw.tokens;

import java.util.HashMap;

public enum TokenKeyword
{
    IF("if", TokenType.IF),
    ELIF("elif", TokenType.ELIF),
    ELSE("else", TokenType.ELSE),
    FOR("for", TokenType.FOR),
    LET("let", TokenType.LET),
    WHERE("where", TokenType.WHERE),
    IN("in", TokenType.IN),
    RETURN("return", TokenType.RETURN),
    BREAK("break", TokenType.BREAK)

    ;
    String key;
    TokenType type;
    TokenKeyword(String key, TokenType type)
    {
        this.key = key;
        this.type = type;
    }
    private static HashMap<String, TokenType> _keywords = new HashMap<>();
    static
    {
        for(TokenKeyword token : values())
        {
            _keywords.put(token.key, token.type);
        }
    }

    public static TokenType get(String key, TokenType defaultValue)
    {
        return _keywords.getOrDefault(key, defaultValue);
    }
}
