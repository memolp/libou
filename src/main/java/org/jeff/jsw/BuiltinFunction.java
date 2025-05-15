package org.jeff.jsw;

public interface BuiltinFunction
{
    Object call(Env env, Object... args);
}
