package org.jeff.web.handlers;


import org.jeff.web.Request;
import org.jeff.web.response.Response;
import org.jeff.web.response.ResponseBuilder;
import org.jeff.web.router.Router;

public class RequestHandler
{
    public RequestHandler() {}

    public Response get(Request request, Router router)
    {
        return ResponseBuilder.build(403);
    }

    public Response post(Request request, Router router)
    {
        return ResponseBuilder.build(403);
    }

    public Response head(Request request, Router router)
    {
        return ResponseBuilder.build(403);
    }
}
