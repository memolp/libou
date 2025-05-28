package org.jeff.web.ws;

import org.jeff.web.HttpContext;
import org.jeff.web.Request;
import org.jeff.web.response.InternalResponse;
import org.jeff.web.response.Response;
import org.jeff.web.router.Router;

public class WebSocketRouter extends Router
{
    private final WebSocketMessage listener;

    public WebSocketRouter(String path, WebSocketMessage listener)
    {
        super();
        this.routerPath = path;
        this.listener = listener;
    }

    @Override
    public boolean match(String path)
    {
        return this.routerPath.equals(path);
    }

    @Override
    public void doRequest(HttpContext context, Request request, Response response)
    {
        if(!request.method.equalsIgnoreCase("get"))
        {
            response.set_status(405, "Method Not Allowed");
            return;
        }
        WebSocketHandler handler = new WebSocketHandler();
        handler.initial(context, this);
        handler.get(request, response);
        if(response.responseStatus.code() != 101)
        {
            return;
        }
        ((InternalResponse)response).upgradeWebSocket(listener);
    }


}
