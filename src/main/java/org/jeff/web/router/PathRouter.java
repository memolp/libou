package org.jeff.web.router;

import org.jeff.web.handlers.RequestHandler;


/**
 * 全路径匹配路由
 * 注册的/test/t => 只能匹配到/test/t； 而/test/t/d 不会匹配成功
 */
public class PathRouter extends Router
{
    public PathRouter(String url, Class<? extends RequestHandler> cls)
    {
        this.routerPath = url;
        this.routerClass = cls;
        this.priority = 0;
    }

    @Override
    public boolean match(String path)
    {
        return this.routerPath.equals(path);
    }


}
