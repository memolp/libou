package org.jeff.web.server;

import java.nio.channels.CompletionHandler;

public class SessionReadHandler implements CompletionHandler<Integer, Session>
{
    @Override
    public void completed(Integer result, Session session)
    {
        session.onRead(result);
    }

    @Override
    public void failed(Throwable exc, Session session)
    {
        session.onReadError(exc);
    }
}
