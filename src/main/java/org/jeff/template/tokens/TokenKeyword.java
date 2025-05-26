package org.jeff.template.tokens;


import java.util.HashMap;

public enum TokenKeyword
{
    IF("if", TokenType.KEY_IF),
    ELIF("elif", TokenType.KEY_ELIF),
    ELSE("else", TokenType.KEY_ELSE),
    FOR("for", TokenType.KEY_FOR),
    WHILE("while", TokenType.KEY_WHILE),
    IN("in", TokenType.KEY_IN),
    BREAK("break", TokenType.KEY_BREAK),
    CONTINUE("continue", TokenType.KEY_CONTINUE),

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
