package org.jeff.web;

import org.jeff.web.handlers.RequestHandler;
import org.jeff.web.parse.RequestParseException;
import org.jeff.web.parse.RequestParser;
import org.jeff.web.parse.RequestParserState;
import org.jeff.web.router.Router;
import org.jeff.web.router.RouterChooser;
import org.jeff.web.server.aio_handlers.ClientReadHandler;
import org.jeff.web.server.aio_handlers.ClientWriteHandler;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Session
{
    private AsynchronousSocketChannel _client;
    private ByteBuffer _buffer = ByteBuffer.allocate(1024);
    private RequestParser _parser = null;
    private ClientReadHandler _reader = new ClientReadHandler();
    private Request _request = null;
    private Response _response = null;
    private RouterChooser _router = null;

    public Session(AsynchronousSocketChannel client, RouterChooser router)
    {
        this._client = client;
        this._router = router;
    }

    public void doHandle()
    {
        this._request = new Request();
        this._parser = new RequestParser(this._request);
        _client.read(_buffer, this, _reader);
    }

    public void onRead(int size)
    {
        if(size <= 0)
        {
            this.doClose();
            return;
        }
        try
        {
            this._buffer.flip();
            RequestParserState res = this._parser.parser(this._buffer);
            this._buffer.clear();
            if(res == RequestParserState.Continue)
            {
                _client.read(_buffer, this, _reader);
                return;
            }
        } catch (RequestParseException e)
        {
            System.out.println(e.toString());
            this.doClose();
            return;
        }
        try
        {
            Router router = this._router.match_route(this._request.path);
            if(router != null)
            {
                this._response = router.doRequest(this, this._request);
            }else
            {
                this._response = new Response(404, "Not Found", "");
            }
        }catch (Exception e)
        {
            this._response = new Response(500, "Server Error", e.getMessage());
        }
        this.doResponse();
    }

    private void doResponse()
    {
        if(this._response == null)
        {
            this.doClose();
            return;
        }
        //this._response.write(this, new ClientWriteHandler());


        StringBuilder header = new StringBuilder();
        header.append(String.format("%s %d %s\r\n", this._response.version, this._response.status, this._response.msg));
        for(String key : this._response.headers.keySet())
        {
            String value = this._response.headers.get(key);
            header.append(String.format("%s: %s\r\n", key, value));
        }
        if(!this._response.headers.containsKey("Connection"))
        {
            header.append("Connection: close");
        }
        if(!this._response.headers.containsKey("Content-Type"))
        {
            header.append("Content-Type: text/plain; charset=UTF-8\r\n");
        }
        if(!this._response.headers.containsKey("Date"))
        {
            header.append(String.format("Date: %s\r\n",new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z").format(new Date())));
        }
        if(!this._response.headers.containsKey("Content-Length"))
        {
            header.append(String.format("Content-Length: %d\r\n", this._response.body.length()));
        }
        header.append("\r\n");

        if(this._response.body.length() > 0)
        {
            header.append(this._response.body);
        }

        ByteBuffer responseBuffer = ByteBuffer.wrap(header.toString().getBytes(StandardCharsets.UTF_8));
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
