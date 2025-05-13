package org.jeff.web.router;

import org.jeff.web.handlers.RequestHandler;


public class PathRouter extends Router
{
    public String routerPath;

    public PathRouter(String url, Class<? extends RequestHandler> cls)
    {
        this.routerPath = url;
        this.routerClass = cls;
    }

    @Override
    public boolean equals(String path)
    {
        return this.routerPath.equals(path);
    }


}
