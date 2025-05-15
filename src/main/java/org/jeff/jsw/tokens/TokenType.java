package org.jeff.jsw.tokens;

public enum TokenType
{
    IDENT, NUMBER, STRING,
    PAREN_OPEN,     // (
    PAREN_CLOSE,    // )
    BRACE_OPEN,     // {
    BRACE_CLOSE,    // }
    SEMICOLON,      // ;
    COMMA,          // ,
    DOT,            // .
    // 定义全部的操作符号
    OP_ASSIGN,         // =
    OP_NOT,           // !
    OP_STAR, OP_SLASH, OP_PERCENT, OP_PLUS, OP_MINUS,          // * / % + -
    OP_LEFT_SHIFT, OP_RIGHT_SHIFT,         // << >>
    OP_LT, OP_LTE, OP_GT, OP_GTE,   // < <= > >=
    OP_EQ, OP_NEQ,        // == !=
    OP_BIT_AND, OP_BIT_OR, OP_BIT_NOR, // & | ~
    OP_AND,            // &&
    OP_OR,           // ||

    LET, IF, FOR,  // keywords
    WHERE,FUNCTION, RETURN,BREAK, CONTINUE,
    IN,
    ELIF,
    ELSE,
    EOF
}
