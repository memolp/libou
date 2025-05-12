package org.jeff.web;

import org.jeff.web.http.HttpServer;

public class Application
{
    public Application()
    {

    }

    public void start_server(String host, int port)
    {
        HttpServer server = new HttpServer(host, port);
        server.start_server(host, port);
    }
}
