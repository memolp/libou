package org.jeff.web.ws;

import org.jeff.web.Request;
import org.jeff.web.handlers.RequestHandler;
import org.jeff.web.response.Response;

public class WebSocketHandler extends RequestHandler
{
    @Override
    public void get(Request request, Response response)
    {
        String connection = request.get_header("Connection");
        String upgrade = request.get_header("Upgrade");
        if(connection != null && upgrade != null)
        {
            if(connection.toLowerCase().contains("upgrade") && upgrade.toLowerCase().equals("websocket"))
            {
                this.doWebSocketHandshake(request, response);
                return;
            }
        }
        response.set_status(403);
    }

    private void doWebSocketHandshake(Request request, Response response)
    {
        String key = request.get_header("Sec-WebSocket-Key");
        if(key == null)
        {
            response.set_status(403);
            return;
        }
        try {
            String accept = WebSocketProtocol.buildAcceptKey(key);
            response.set_status(101);
            response.set_header("Sec-WebSocket-Accept", accept);
            response.set_header("Connection", "Upgrade");
            response.set_header("Upgrade", "WebSocket");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void head(Request request, Response response)
    {
        response.set_status(403);
    }

    @Override
    public void post(Request request, Response response)
    {
        response.set_status(403);
    }
}
