package org.jeff.web.server;

import org.jeff.web.Application;
import org.jeff.web.HttpContext;
import org.jeff.web.router.RouterChooser;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer
{
    public final String host;
    public final int port;
    public final HttpContext context;

    private ExecutorService _serverThreadPool = null;
    private AsynchronousChannelGroup _serverGroup = null;
    private AsynchronousServerSocketChannel _serverChannel = null;
    private final ServerAcceptHandler _acceptHandler = new ServerAcceptHandler();

    public HttpServer(HttpContext context, String host, int port)
    {
        this.context = context;
        this.host = host;
        this.port = port;
    }

    protected void setupServer(String host, int port) throws IOException
    {
        this._serverThreadPool = Executors.newFixedThreadPool(this.context.maxThreadNum);
        this._serverGroup = AsynchronousChannelGroup.withThreadPool(this._serverThreadPool);
        this._serverChannel = AsynchronousServerSocketChannel.open(this._serverGroup);
        this._serverChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        this._serverChannel.bind(new InetSocketAddress(host, port));
    }

    protected void doAccept()
    {
        this._serverChannel.accept(this, this._acceptHandler);
    }

    public void onAcceptError(Throwable e)
    {

    }

    public void shutdown()
    {
        try
        {
            this._serverThreadPool.shutdownNow();
            this._serverGroup.shutdownNow();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void start() throws IOException
    {
        this.setupServer(this.host, this.port);
        this.doAccept();
    }

    public void onAccept(AsynchronousSocketChannel client)
    {
        this.doAccept();
        try
        {
            client.setOption(StandardSocketOptions.TCP_NODELAY, true);
            client.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
            Session session = new Session(this.context, client);
            session.doHandle();
        }catch (Exception ignored)
        {
        }
    }
}
