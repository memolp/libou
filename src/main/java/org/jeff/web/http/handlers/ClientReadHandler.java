package org.jeff.web.http.handlers;

import org.jeff.web.http.Session;

import java.nio.channels.CompletionHandler;

public class ClientReadHandler implements CompletionHandler<Integer, Session>
{
    @Override
    public void completed(Integer result, Session session)
    {
        session.onRead(result);
    }

    @Override
    public void failed(Throwable exc, Session session)
    {
        session.onRead(0);
    }
}
