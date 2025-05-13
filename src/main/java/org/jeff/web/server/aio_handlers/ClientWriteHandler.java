package org.jeff.web.server.aio_handlers;

import org.jeff.web.Session;

import java.nio.channels.CompletionHandler;

public class ClientWriteHandler implements CompletionHandler<Integer, Session>
{
    @Override
    public void completed(Integer result, Session session)
    {
        session.onWrite(result);
    }

    @Override
    public void failed(Throwable exc, Session session)
    {
        session.onWrite(0);
    }
}