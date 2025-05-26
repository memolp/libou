package org.jeff.web.router;

import org.jeff.web.HttpContext;
import org.jeff.web.Request;
import org.jeff.web.handlers.RequestHandler;
import org.jeff.web.response.Response;

import java.lang.reflect.Method;

/**
 * 注册的路由类，关联请求方法类
 */
public abstract class Router implements Comparable<Router>
{
    /** 保持请求处理类 */
    public Class<? extends RequestHandler> routerClass;
    public String routerPath;
    /** 路由的优先级 */
    public int priority = 0;
    /** 所以的路由都会通过这个方法进行匹配，返回true则表示匹配成功 */
    public abstract boolean match(String path);

    public int compareTo(Router o)
    {
        return this.routerPath.compareTo(o.routerPath);
    }

    /** 创建对应的处理类对象进行处理 */
    public void doRequest(HttpContext context, Request request, Response response)
    {
        try {
            RequestHandler inst = this.routerClass.newInstance();
            inst.initial(context, this);
            Method method = this.routerClass.getMethod(request.method.toLowerCase(), Request.class, Response.class);
            method.invoke(inst, request, response);
        } catch (Exception e)
        {
            response.set_status(500, e.toString());
        }
    }
}
