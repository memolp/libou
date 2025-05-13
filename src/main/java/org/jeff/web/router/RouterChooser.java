package org.jeff.web.router;

import org.jeff.web.handlers.RequestHandler;

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

    public void add_route(Router router)
    {
        this._routers.add(router);
    }

    /**
     * 返回匹配的处理类
     * @param path 请求的URL地址不包含参数
     * @return 返回null表示没有匹配到
     */
    public Router match_route(String path)
    {
        for(Router r : this._routers)
        {
            if(r.equals(path)) return r;
        }
        return null;
    }
}
