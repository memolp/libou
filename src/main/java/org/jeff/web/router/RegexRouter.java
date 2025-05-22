package org.jeff.web.router;

import org.jeff.web.handlers.RequestHandler;

import java.util.regex.Pattern;

/**
 * 正则路由匹配，通过传递的正则进行匹配
 */
public class RegexRouter extends Router
{
    /**
     * 正则匹配的路由
     * @param rex
     * @param cls
     */
    public RegexRouter(String rex, Class<? extends RequestHandler> cls)
    {
        this.routerPath = rex;
        this.routerClass = cls;
        this.priority = 1;
    }

    @Override
    public boolean match(String path)
    {
        return Pattern.matches(routerPath, path);
    }
}
