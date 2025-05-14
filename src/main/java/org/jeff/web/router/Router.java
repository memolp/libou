package org.jeff.web.router;

import org.jeff.web.Request;
import org.jeff.web.response.Response;
import org.jeff.web.Session;
import org.jeff.web.handlers.RequestHandler;
import org.jeff.web.response.ResponseBuilder;

import java.lang.reflect.Method;

public abstract class Router
{
    public Class<? extends RequestHandler> routerClass;

    public abstract boolean equals(String path);
    public Response doRequest(Session session, Request request)
    {
        try {
            RequestHandler inst = this.routerClass.newInstance();
            Method method = this.routerClass.getMethod(request.method.toLowerCase(), Request.class, Router.class);
            return (Response) method.invoke(inst, request, this);
        } catch (Exception e)
        {
            return ResponseBuilder.build(500).write(e.getMessage());
        }
    }
}
