package org.jeff.web;

import org.jeff.web.parse.RequestParseException;
import org.jeff.web.parse.RequestParser;
import org.jeff.web.parse.RequestParserState;
import org.jeff.web.response.*;
import org.jeff.web.router.Router;
import org.jeff.web.router.RouterChooser;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class Session
{
    private AsynchronousSocketChannel _client;
    private ByteBuffer _buffer = ByteBuffer.allocate(10240);
    private RequestParser _parser = null;
    private RouterChooser _router = null;

    public Session(AsynchronousSocketChannel client, RouterChooser router)
    {
        this._client = client;
        this._router = router;
    }

    public void doHandle()
    {
        if(!_client.isOpen()) return;
        this._parser = new RequestParser();
        this._buffer.clear();
        _client.read(_buffer, this, new CompletionHandler<Integer, Session>()
        {
            @Override
            public void completed(Integer size, Session session)
            {
                if(size <= 0)
                {
                    doClose();
                    return;
                }
                try
                {
                    _buffer.flip();
                    RequestParserState res = _parser.parser(_buffer);
                    if (res == RequestParserState.Continue)
                    {
                        _buffer.clear();
                        _client.read(_buffer, null, this);
                        return;
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                try {
                    Request request = _parser.request;
                    Router router = _router.match_route(request.path);
                    if (router != null) {
                        send(router.doRequest(session, request));
                    } else {
                        send(ResponseBuilder.build(404));
                    }
                } catch (Exception e) {
                    send(ResponseBuilder.build(500).write(e.getMessage()));
                }
            }

            @Override
            public void failed(Throwable exc, Session session)
            {
                throw new RuntimeException(exc);
            }
        });
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
