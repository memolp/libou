package org.jeff.web.response;

import org.jeff.web.HttpContext;
import org.jeff.web.server.Session;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class WriteHeaderHandler implements CompletionHandler<Integer, ByteBuffer>
{
    private final Session session;
    private final InternalResponse response;
    private final WriteBodyHandler bodyHandler;
    public WriteHeaderHandler(Session session, InternalResponse response, WriteBodyHandler bodyHandler)
    {
        this.session = session;
        this.response = response;
        this.bodyHandler = bodyHandler;
    }

    public void doSendHeader(HttpContext context)
    {
        String header = response.build_header(context);
        ByteBuffer buffer = ByteBuffer.wrap(header.getBytes());
        this.session.write(buffer, buffer, this);
    }

    @Override
    public void completed(Integer result, ByteBuffer attachment)
    {
        if(attachment.hasRemaining())
        {
            this.session.write(attachment, attachment, this);
            return;
        }
        this.bodyHandler.doSendBody();
    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment)
    {
        this.session.onResponseFailed(this.response, exc);
    }
}
