package org.jeff.web.handlers;


import org.jeff.web.Request;
import org.jeff.web.Response;
import org.jeff.web.Session;

public class RequestHandler
{
    public Request request;
    public Response response;
    public Session session;

    public RequestHandler() {}

    public void initialHandler(Request request, Session session)
    {
        this.request = request;
        this.session = session;
        this.response = setupResponse();
    }

    protected Response setupResponse()
    {
        return new Response();
    }

    public void get()
    {
        this.response.set_status(403, "ss", "");
    }

    public void post()
    {
        this.response.set_status(403, "ss", "");
    }

    public void head()
    {
        this.response.set_status(403, "ss", "");
    }
}
