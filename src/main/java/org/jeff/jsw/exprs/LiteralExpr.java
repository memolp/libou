package org.jeff.jsw.exprs;

import org.jeff.jsw.JsContext;
import org.jeff.jsw.objs.JsObject;

/**
 * 常量
 * 1. 字符串
 * 2. 数字
 */
public class LiteralExpr implements Expression
{
    private final JsObject value;

    public LiteralExpr(JsObject value) {
        this.value = value;
    }

    public JsObject eval(JsContext context) {
        return value;
    }

    @Override
    public String toString()
    {
        return "[LiteralExpr]:" + value;
    }
}
