package org.jeff.web.handlers;

import org.jeff.web.FileResponse;
import org.jeff.web.Response;

public class StaticRequestHandler extends RequestHandler
{
    public boolean support_cache = true;
    public boolean support_range = true;

    @Override
    protected Response setupResponse()
    {
        return new FileResponse();
    }

    @Override
    public void get()
    {

    }
}
