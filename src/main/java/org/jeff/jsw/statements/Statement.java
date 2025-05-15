package org.jeff.jsw.statements;

import org.jeff.jsw.Env;

/**
 * 语句接口
 */
public interface Statement
{
    /**
     * 执行语句，并返回最后一次语句的结果
     * @param env
     * @param args
     * @return
     */
    Object execute(Env env, Object...args);
}
