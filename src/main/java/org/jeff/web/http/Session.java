package org.jeff.web.http;

import org.jeff.web.http.handlers.ClientReadHandler;
import org.jeff.web.http.handlers.ClientWriteHandler;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;

public class Session
{
    private AsynchronousSocketChannel _client;
    private ByteBuffer _buffer = ByteBuffer.allocate(1024);
    private RequestParser _parser = null;
    private ClientReadHandler _reader = new ClientReadHandler();
    public Session(AsynchronousSocketChannel client)
    {
        this._client = client;
    }

    public void doHandle()
    {
        this._parser = new RequestParser();
        _client.read(_buffer, this, _reader);
    }

    public void onRead(int size)
    {
        if(size <= 0)
        {
            this.doClose();
            return;
        }
        RequestParserState res = this._parser.parser();
        if(res == RequestParserState.Error)
        {
            this.doClose();
            return;
        }else if(res == RequestParserState.Continue)
        {
            _client.read(_buffer, this, _reader);
            return;
        }
        // TODO 执行HTTP响应

        String httpResponse = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/plain; charset=UTF-8\r\n" +
                "Content-Length: 16\r\n" +
                "Connection: close\r\n\r\n" +
                "Hello, AIO HTTP!";
        ByteBuffer responseBuffer = ByteBuffer.wrap(httpResponse.getBytes(StandardCharsets.UTF_8));
        _client.write(responseBuffer, this, new ClientWriteHandler());
    }

    public void onWrite(int size)
    {
        this.doClose();
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
}
