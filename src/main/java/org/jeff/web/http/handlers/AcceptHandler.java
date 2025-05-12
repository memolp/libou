package org.jeff.web.http.handlers;

import org.jeff.web.http.Session;

import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.logging.Logger;

public class AcceptHandler implements CompletionHandler<AsynchronousSocketChannel, AsynchronousServerSocketChannel>
{
    private Logger _logger = Logger.getLogger(AcceptHandler.class.getName());

    @Override
    public void completed(AsynchronousSocketChannel client, AsynchronousServerSocketChannel serverChannel)
    {
        serverChannel.accept(serverChannel, this);
        Session session = new Session(client);
        _logger.info("Client connected");
        session.doHandle();
    }

    @Override
    public void failed(Throwable exc, AsynchronousServerSocketChannel serverChannel)
    {
        _logger.info("Accept failed");
        if(serverChannel.isOpen())
            serverChannel.accept(serverChannel, this);
    }
}