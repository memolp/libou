package org.jeff.jsw.exprs;

import org.jeff.jsw.Env;

/**
 * 常量
 * 1. 字符串
 * 2. 数字
 */
public class LiteralExpr implements Expression
{
    private final Object value;

    public LiteralExpr(Object value) {
        this.value = value;
    }

    public Object eval(Env env) {
        return value;
    }

    @Override
    public String toString()
    {
        return "[LiteralExpr]:" + value;
    }
}
