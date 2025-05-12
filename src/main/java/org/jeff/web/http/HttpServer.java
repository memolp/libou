package org.jeff.web.http;

import org.jeff.web.http.handlers.AcceptHandler;

import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class HttpServer
{
    public String host = "0.0.0.0";
    public int port = 80;
    public int ThreadNum = 4;

    private ExecutorService _serverThreadPool = null;
    private AsynchronousChannelGroup _serverGroup = null;
    private AsynchronousServerSocketChannel _serverChannel = null;
    private Logger _logger = Logger.getLogger(HttpServer.class.getName());

    public HttpServer()
    {

    }

    public HttpServer(String host, int port)
    {
        this.host = host;
        this.port = port;
    }

    private void init() throws Exception
    {
        this._serverThreadPool = Executors.newFixedThreadPool(this.ThreadNum);
        this._serverGroup = AsynchronousChannelGroup.withThreadPool(this._serverThreadPool);
        this._serverChannel = AsynchronousServerSocketChannel.open(this._serverGroup);
        this._serverChannel.bind(new InetSocketAddress(this.host, this.port));
        this._logger.info("Server started on " + this.host + ":" + this.port);
        this._serverChannel.accept(this._serverChannel, new AcceptHandler());
    }

    public void start_server(String host, int port)
    {
        this.host = host;
        this.port = port;
        this.start_server();
    }

    public void start_server()
    {
        try {
            this.init();
        }catch (Exception e)
        {
            this._logger.severe(e.getMessage());
        }
    }


}
