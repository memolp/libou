package org.jeff.web.http;

public class RequestBody
{
    public RequestBodyType bodyType = RequestBodyType.Unkown;

    public RequestBody(RequestBodyType type)
    {
        this.bodyType = type;
    }


}
