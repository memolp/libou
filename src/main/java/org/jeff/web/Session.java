package org.jeff.web;

import org.jeff.web.parse.RequestParseException;
import org.jeff.web.parse.RequestParser;
import org.jeff.web.parse.RequestParserState;
import org.jeff.web.response.*;
import org.jeff.web.router.Router;
import org.jeff.web.router.RouterChooser;
import org.jeff.web.server.aio_handlers.ClientReadHandler;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class Session
{
    private AsynchronousSocketChannel _client;
    private ByteBuffer _buffer = ByteBuffer.allocate(1024);
    private RequestParser _parser = null;
    private ClientReadHandler _reader = new ClientReadHandler();
    private RouterChooser _router = null;

    public Session(AsynchronousSocketChannel client, RouterChooser router)
    {
        this._client = client;
        this._router = router;
    }

    public void doHandle()
    {
        this._parser = new RequestParser();
        this._buffer.clear();
        _client.read(_buffer, this, _reader);
    }

    public void onRead(int size)
    {
        if (size <= 0) {
            this.doClose();
            return;
        }
        try {
            this._buffer.flip();
            RequestParserState res = this._parser.parser(this._buffer);
            this._buffer.clear();
            if (res == RequestParserState.Continue) {
                _client.read(_buffer, this, _reader);
                return;
            }
        } catch (RequestParseException e) {
            System.out.println(e.toString());
            this.doClose();
            return;
        }
        try {
            Request request = this._parser.request;
            Router router = this._router.match_route(request.path);
            if (router != null) {
                this.send(router.doRequest(this, request));
            } else {
                this.send(ResponseBuilder.build(404));
            }
        } catch (Exception e) {
            this.send(ResponseBuilder.build(500).write(e.getMessage()));
        }
    }

    protected void send(Response response)
    {
        if (!(response instanceof InternalResponse))
        {
            this.doClose();
            return;
        }
        InternalResponse resp = (InternalResponse)response;
        String header = resp.build_header();
        ByteBuffer buffer = ByteBuffer.wrap(header.getBytes());
        this._client.write(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>()
        {
            @Override
            public void completed(Integer result, ByteBuffer attachment)
            {
                if(attachment.hasRemaining()) _client.write(attachment, attachment, this);
                onWriteHeaderCompleted(resp);
            }
            @Override
            public void failed(Throwable exc, ByteBuffer attachment)
            {
                onWriteHeaderError(exc, resp);
            }
        });
    }

    public void doClose()
    {
        try
        {
            this._client.close();
        }catch (Exception e)
        {

        }
    }

    protected void onWriteHeaderCompleted(InternalResponse response)
    {
        if(!response.include_body())
        {
            this.onCompleted(response);
            return;
        }
        this.doWriteChunkBody(response);
    }

    protected void onWriteHeaderError(Throwable e, InternalResponse response)
    {
        this.onError(e, response);
    }

    protected void onWriteBodyCompleted(InternalResponse response)
    {
        this.doWriteChunkBody(response);
    }

    protected void doWriteChunkBody(InternalResponse response)
    {
        ByteBuffer buffer = response.next_trunk();
        if(buffer == null)
        {
            this.onCompleted(response);
            return;
        }
        _client.write(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>()
        {
            @Override
            public void completed(Integer result, ByteBuffer attachment)
            {
                if(attachment.hasRemaining()) _client.write(attachment, attachment, this);
                onWriteBodyCompleted(response);
            }
            @Override
            public void failed(Throwable exc, ByteBuffer attachment)
            {
                onWriteBodyError(exc, response);
            }
        });
    }

    protected void onWriteBodyError(Throwable e, InternalResponse response)
    {
        this.onError(e, response);
    }

    protected void onCompleted(InternalResponse response)
    {
        if(!response.keepAlive())
            this.doClose();
        else
            this.doHandle();
    }

    protected void onError(Throwable e, InternalResponse response)
    {
        System.out.println(response);
        e.printStackTrace();
        this.doClose();
    }
}
