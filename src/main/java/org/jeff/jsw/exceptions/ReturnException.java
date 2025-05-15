package org.jeff.jsw.exceptions;

public class ReturnException extends RuntimeException
{
    public Object value = null;
    public ReturnException(Object value)
    {
        this.value = value;
    }
}
