package org.jeff.web.server;

import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class ServerAcceptHandler implements CompletionHandler<AsynchronousSocketChannel, HttpServer>
{
    @Override
    public void completed(AsynchronousSocketChannel client, HttpServer server)
    {
        server.onAccept(client);
    }

    @Override
    public void failed(Throwable exc, HttpServer server)
    {
        server.onAcceptError(exc);
    }
}
