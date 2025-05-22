package org.jeff.web.router;

import org.jeff.web.handlers.StaticRequestHandler;

/**
 * 资源路由匹配
 * 注册 /static/ 那么所有以/static/开头的都会匹配成功 /static/a/b/c/d 全部都可以
 */
public class ResourceRouter extends Router
{
    public String resLocalRoot;

    /**
     * 静态资源处理路由， 处理类为StaticRequestHandler
     * @param path
     * @param root
     */
    public ResourceRouter(String path, String root)
    {
        this.routerPath = path;
        this.resLocalRoot = root;
        this.routerClass = StaticRequestHandler.class;
        this.priority = 2;
    }

    /**
     * 自实现处理类
     * @param path
     * @param root
     * @param cls
     */
    public ResourceRouter(String path, String root, Class<? extends StaticRequestHandler> cls)
    {
        this.routerPath = path;
        this.resLocalRoot = root;
        this.routerClass = cls;
    }

    @Override
    public boolean match(String path)
    {
        return path.startsWith(routerPath);
    }

}
