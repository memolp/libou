package org.jeff.web.handlers;


import org.jeff.web.HttpContext;
import org.jeff.web.Request;
import org.jeff.web.response.Response;
import org.jeff.web.router.Router;

public class RequestHandler
{
    public HttpContext context;
    public Router router;

    public RequestHandler() {}

    public void initial(HttpContext context, Router router)
    {
        this.context = context;
        this.router = router;
    }

    public void get(Request request, Response response)
    {

    }

    public void post(Request request, Response response)
    {

    }

    public void head(Request request, Response response)
    {

    }
}
