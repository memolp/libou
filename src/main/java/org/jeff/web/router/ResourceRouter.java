package org.jeff.web.router;

import org.jeff.web.handlers.StaticRequestHandler;

public class ResourceRouter extends Router
{
    public String resPath;
    public String resLocalRoot;

    public ResourceRouter(String path, String root)
    {
        this.resPath = path;
        this.resLocalRoot = root;
        this.routerClass = StaticRequestHandler.class;
    }
    public ResourceRouter(String path, String root, Class<? extends StaticRequestHandler> cls)
    {
        this.resPath = path;
        this.resLocalRoot = root;
        this.routerClass = cls;
    }

    @Override
    public boolean equals(String path)
    {
        return path.startsWith(resPath);
    }

}
