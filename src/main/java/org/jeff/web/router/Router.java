package org.jeff.web.router;

import org.jeff.web.HttpContext;
import org.jeff.web.Request;
import org.jeff.web.handlers.RequestHandler;
import org.jeff.web.response.IResponse;
import org.jeff.web.response.Response;
import org.jeff.web.server.Session;

import java.lang.reflect.Method;

public abstract class Router
{
    public Class<? extends RequestHandler> routerClass;

    public abstract boolean equals(String path);

    public void doRequest(HttpContext context, Request request, Response response)
    {
        try {
            RequestHandler inst = this.routerClass.newInstance();
            inst.initial(context, this);
            Method method = this.routerClass.getMethod(request.method.toLowerCase(), Request.class, Response.class);
            method.invoke(inst, request, response);
        } catch (Exception e)
        {
            response.set_status(500, e.getMessage());
        }
    }
}
