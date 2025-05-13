package org.jeff.web.router;

import org.jeff.web.Request;
import org.jeff.web.Response;
import org.jeff.web.Session;
import org.jeff.web.handlers.RequestHandler;

import java.lang.reflect.Method;

public abstract class Router
{
    public Class<? extends RequestHandler> routerClass;

    public abstract boolean equals(String path);
    public Response doRequest(Session session, Request request)
    {
        try {
            RequestHandler inst = this.routerClass.newInstance();
            inst.initialHandler(request, session);
            Method method = this.routerClass.getMethod(request.method.toLowerCase());
            method.invoke(inst);
            return inst.response;
        } catch (Exception e)
        {
            return new Response(500, "Server errpr", e.getMessage());
        }
    }
}
