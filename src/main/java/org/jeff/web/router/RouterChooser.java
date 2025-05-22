package org.jeff.web.router;

import org.jeff.web.HttpContext;
import org.jeff.web.Request;
import org.jeff.web.handlers.RequestHandler;
import org.jeff.web.response.Response;

import java.util.*;

/**
 * 路由选择器，用于将客户端的请求按照注册的路由进行匹配
 * 采用优先级匹配。 PathRouter > RegexRouter > ResourceRouter
 */
public class RouterChooser
{
    private final SortedSet<Router> _routers;

    public RouterChooser()
    {
        _routers = new TreeSet<>();
    }
    /** 添加一个路径匹配的路由处理 */
    public void add_route(String path, Class<? extends RequestHandler> cls)
    {
        this._routers.add(new PathRouter(path, cls));
    }
    /** 添加一个正则匹配的路由 */
    public void add_regex(String rex, Class<? extends RequestHandler> cls)
    {
        this._routers.add(new RegexRouter(rex, cls));
    }
    /** 添加一个静态资源处理路由 */
    public void add_static(String path, String filepath)
    {
        this._routers.add(new ResourceRouter(path, filepath));
    }
    /** 添加一个路由处理，外部创建的路由 */
    public void add_route(Router router)
    {
        this._routers.add(router);
    }
    /**
     * 返回匹配的处理类
     * @param path 请求的URL地址不包含参数
     * @return 返回null表示没有匹配到
     */
    protected Router match_route(String path)
    {
        for(Router r : this._routers)
        {
            if(r.match(path)) return r;
        }
        return null;
    }
    /** 寻找路由进行请求处理 */
    public void doRequest(HttpContext context, Request request, Response response)
    {
        Router router = this.match_route(request.path);
        if(router == null)
        {
            response.set_status(404, "Not Found");
            return;
        }
        router.doRequest(context, request, response);
    }
}
