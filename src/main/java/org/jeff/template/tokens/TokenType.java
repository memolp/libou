package org.jeff.template.tokens;

import java.util.HashMap;
import java.util.Map;

public enum TokenType
{
    IDENTIFIER,
    INT, FLOAT, STRING,                                 // 基础数据类型：int、float、string
    // 双目运算符
    OP_PLUS, OP_MINUS, OP_MULTI, OP_DIV, OP_MOD,        // 基本的运算符 + - * / %
    OP_EQ, OP_NE, OP_LT, OP_LE, OP_GT, OP_GE,           // 关系运算符 == != < <= > >=
    OP_AND, OP_OR,                                      // 逻辑运算符 &&  ||
    OP_BIT_AND, OP_BIT_OR, OP_BIT_XOR,                  // 按位与、或、异或
    OP_BIT_LEFT, OP_BIT_RIGHT,                          // << >>
    // 赋值运算符
    OP_ASSIGN,                                          // 赋值 =
    OP_PLUS_ASSIGN, OP_MINUS_ASSIGN,                    // += -=
    OP_MULTI_ASSIGN, OP_DIV_ASSIGN, OP_MOD_ASSIGN,      // *= /= %=
    // 单目运算
    OP_INC, OP_DEC,                                     // 高级 ++  -- 运算
    OP_NOT,                                             // 逻辑运算符 !
    OP_BIT_NOT,                                         // ~ 取反运算
    // 其他符号
    DOT, COMMA, SEMICOLON,COLON,                        // . , ; :
    LPAREN, RPAREN,                                     // ( )
    LBRACKET, RBRACKET,                                 // [ ]
    LBRACE, RBRACE,                                     // { }
    // 关键字
    KEY_IF, KEY_ELIF, KEY_ELSE,                         // if elif else
    KEY_FOR, KEY_IN,                                    // for in
    KEY_WHILE,                                          // while
    KEY_BREAK, KEY_CONTINUE,                            // break continue
    EOF,
}
