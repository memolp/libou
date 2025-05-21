package org.jeff.web;

import org.jeff.web.router.Router;
import org.jeff.web.router.RouterChooser;
import org.jeff.web.server.HttpServer;

/**
 * 快捷的运行
 */
public class Application
{
    public HttpContext context;
    public RouterChooser router;

    public Application()
    {
        this.context = new HttpContext();
        this.router = new RouterChooser();
        this.context.chooser = this.router;
        this.context.application = this;
    }

    protected void init(HttpContext context)
    {

    }

    protected void destroy(HttpContext context)
    {

    }

    public void start(String host, int port)
    {
        HttpServer server = new HttpServer(this.context, host, port);
        try
        {
            this.init(this.context);
            server.start();
            Thread.currentThread().join();
        } catch (Exception e)
        {
            server.shutdown();
        }
        this.destroy(this.context);
    }
}
