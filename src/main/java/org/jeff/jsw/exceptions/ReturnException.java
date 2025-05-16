package org.jeff.jsw.exceptions;

import org.jeff.jsw.objs.JsObject;

public class ReturnException extends RuntimeException
{
    public JsObject value = null;
    public ReturnException(JsObject value)
    {
        this.value = value;
    }
}
