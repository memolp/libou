package org.jeff.web.ws;

public interface WebSocketMessage
{
    void onConnected(WebSocketSession session);
    void onDisconnected(WebSocketSession session);
    void onMessage(WebSocketSession session, int type, byte[] data);
}
