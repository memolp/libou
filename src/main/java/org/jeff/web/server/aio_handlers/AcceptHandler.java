package org.jeff.web.server.aio_handlers;

import org.jeff.web.Session;
import org.jeff.web.server.HttpServer;

import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.logging.Logger;

public class AcceptHandler implements CompletionHandler<AsynchronousSocketChannel, HttpServer>
{
    private Logger _logger = Logger.getLogger(AcceptHandler.class.getName());

    @Override
    public void completed(AsynchronousSocketChannel client, HttpServer server)
    {
        server.doAccept();
        Session session = new Session(client, server.get_runner().router);
        _logger.info("Client connected");
        session.doHandle();
    }

    @Override
    public void failed(Throwable exc, HttpServer server)
    {
        _logger.info("Accept failed");
//        if(serverChannel.isOpen())
//            serverChannel.accept(serverChannel, this);
    }
}