package org.jeff.web.router;

import org.jeff.web.handlers.RequestHandler;

import java.util.regex.Pattern;

public class RegexRouter extends Router
{
    public String rexPath;

    public RegexRouter(String rex, Class<? extends RequestHandler> cls)
    {
        this.rexPath = rex;
        this.routerClass = cls;
    }

    @Override
    public boolean equals(String path)
    {
        return Pattern.matches(rexPath, path);
    }
}
