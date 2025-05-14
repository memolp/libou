package org.jeff.web.server;

import org.jeff.web.Session;
import org.jeff.web.runner.BaseRunner;

import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
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
        this._serverChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>()
        {
            @Override
            public void completed(AsynchronousSocketChannel client, Object attachment)
            {
                _serverChannel.accept(null, this);
                try
                {
                    client.setOption(StandardSocketOptions.TCP_NODELAY, true);
                    //client.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
                    client.setOption(StandardSocketOptions.SO_REUSEADDR, true);
                    Session session = new Session(client, get_runner().router);
                    session.doHandle();
                } catch (Exception e) {
                    _logger.info("Accept failed");
                }
            }

            @Override
            public void failed(Throwable exc, Object attachment)
            {
                _logger.info("Accept failed");
            }
        });
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
