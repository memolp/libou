package org.jeff.jsw.exprs;

import org.jeff.jsw.Env;

public interface Expression
{
    Object eval(Env env);
}
