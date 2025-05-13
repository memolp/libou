package org.jeff.web.parse;

public class RequestParseException extends Exception
{
    public RequestParseException(String message)
    {
        super(message);
    }

    public RequestParseException(String fmt, Object... args)
    {
        super(String.format(fmt, args));
    }
}
