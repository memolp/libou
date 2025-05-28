package org.jeff.web.ws;

import org.jeff.core.BinaryBuffer;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class WebSocketFrame
{
    public int opcode;
    public String text;
    public boolean fin;
    public boolean masked;
    public byte[] bin;

    public WebSocketFrame()
    {

    }



    public static WebSocketFrame decode(ByteBuffer buffer)
    {
        byte b1 = buffer.get();
        byte b2 = buffer.get();
        int opcode = b1 & 0x0F;
        int len = b2 & 0x7F;

        if (len == 126)
        {
            len = ((buffer.get() & 0xFF) << 8) | (buffer.get() & 0xFF);
        }

        byte[] mask = new byte[4];
        buffer.get(mask);
        byte[] payload = new byte[len];
        buffer.get(payload);
        for (int i = 0; i < len; i++) payload[i] ^= mask[i % 4];

        WebSocketFrame frame = new WebSocketFrame();
        frame.opcode = opcode;
        frame.text = new String(payload, StandardCharsets.UTF_8);
        return frame;
    }

    public static ByteBuffer encode(String text) {
        byte[] data = text.getBytes(StandardCharsets.UTF_8);
        int len = data.length;
        ByteBuffer buffer = ByteBuffer.allocate(len + 10);

        buffer.put((byte) 0x81); // FIN + text frame
        if (len <= 125) {
            buffer.put((byte) len);
        } else {
            buffer.put((byte) 126);
            buffer.putShort((short) len);
        }
        buffer.put(data);
        buffer.flip();
        return buffer;
    }
}
