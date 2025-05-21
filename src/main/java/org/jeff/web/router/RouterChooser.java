package org.jeff.web.router;

import org.jeff.web.HttpContext;
import org.jeff.web.Request;
import org.jeff.web.handlers.RequestHandler;
import org.jeff.web.response.Response;

import java.util.HashMap;
import java.util.LinkedList;

public class RouterChooser
{
    private LinkedList<Router> _routers = new LinkedList<>();

    public void add_route(String path, Class<? extends RequestHandler> cls)
    {
        this._routers.add(new PathRouter(path, cls));
    }

    public void add_static(String path, String filepath)
    {
        this._routers.add(new ResourceRouter(path, filepath));
    }

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
            if(r.equals(path)) return r;
        }
        return null;
    }
}
