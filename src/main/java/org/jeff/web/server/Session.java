package org.jeff.web.server;

import org.jeff.web.HttpContext;
import org.jeff.web.Request;
import org.jeff.web.parse.RequestParser;
import org.jeff.web.parse.RequestParserState;
import org.jeff.web.response.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class Session
{
    protected final AsynchronousSocketChannel client;
    protected final SessionReadHandler readHandler = new SessionReadHandler();
    protected final HttpContext context;
    protected final ByteBuffer readBuffer = ByteBuffer.allocate(10240);
    protected RequestParser parser = null;

    public Session(HttpContext context, AsynchronousSocketChannel client)
    {
        this.context = context;
        this.client = client;
    }

    public void doHandle()
    {
        if(!this.client.isOpen()) return;
        this.parser = new RequestParser();
        this.readBuffer.clear();
        this.client.read(this.readBuffer, this, this.readHandler);
    }

    public void onRead(int size)
    {
        if(size < 0)
        {
            this.doClose();
            return;
        }
        this.readBuffer.flip();
        try
        {
            RequestParserState state = this.parser.parser(this.readBuffer);
            if(state == RequestParserState.Continue)
            {
                this.doHandle();
                return;
            }
            this.onRequest(this.parser.request, new InternalResponse());
        } catch (Exception e)
        {
            this.doClose();
        }
    }

    public void onReadError(Throwable e)
    {
        e.printStackTrace();
        this.doClose();
    }

    private void onRequest(Request request, InternalResponse response)
    {
        try
        {
            this.context.chooser.doRequest(this.context, request, response);
            this.doResponse(request, response);
        }catch (Exception e)
        {
            this.doClose();
        }
    }

    protected void doClose()
    {
        try
        {
            this.client.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void doResponse(Request request, InternalResponse response)
    {
        WriteHeaderHandler handler = new WriteHeaderHandler(this, response, new WriteBodyHandler(this, response));
        handler.doSendHeader(this.context);
    }

    /** 用于异步写入数据 */
    public <A> void write(ByteBuffer buffer, A attachment, CompletionHandler<Integer, A> handler)
    {
        this.client.write(buffer, attachment, handler);
    }

    public void onResponseCompleted(InternalResponse response)
    {
        if(!response.keepAlive())
            this.doClose();
        else
            this.doHandle();
    }

    public void onResponseFailed(InternalResponse response, Throwable e)
    {
        System.out.println(response);
        e.printStackTrace();
        this.doClose();
    }
}
