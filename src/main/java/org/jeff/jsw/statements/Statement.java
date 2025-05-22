package org.jeff.jsw.statements;

import org.jeff.jsw.JsContext;
import org.jeff.jsw.objs.JsObject;

/**
 * 语句接口
 */
public interface Statement
{
    /**
     * 执行语句
     * @param context
     * @return
     */
    JsObject execute(JsContext context);
}
