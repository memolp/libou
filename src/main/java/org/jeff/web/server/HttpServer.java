package org.jeff.web.server;

import org.jeff.web.runner.BaseRunner;
import org.jeff.web.server.aio_handlers.AcceptHandler;

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
    private BaseRunner _runner = null;

    public HttpServer(BaseRunner runner)
    {
        this._runner = runner;
    }

    public HttpServer(BaseRunner runner, String host, int port)
    {
        this._runner = runner;
        this.host = host;
        this.port = port;
    }

    public BaseRunner get_runner()
    {
        return this._runner;
    }

    private void init() throws Exception
    {
        this._serverThreadPool = Executors.newFixedThreadPool(this.ThreadNum);
        this._serverGroup = AsynchronousChannelGroup.withThreadPool(this._serverThreadPool);
        this._serverChannel = AsynchronousServerSocketChannel.open(this._serverGroup);
        this._serverChannel.bind(new InetSocketAddress(this.host, this.port));
        this._logger.info("Server started on " + this.host + ":" + this.port);
    }

    public void doAccept()
    {
        this._serverChannel.accept(this, new AcceptHandler());
    }

    public void start(String host, int port)
    {
        this.host = host;
        this.port = port;
        this.start();
    }

    public void start()
    {
        try {
            this.init();
            this.doAccept();
        }catch (Exception e)
        {
            this._logger.severe(e.getMessage());
        }
    }



}
