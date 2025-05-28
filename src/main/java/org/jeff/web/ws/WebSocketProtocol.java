package org.jeff.web.ws;

import org.jeff.core.BinaryBuffer;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class WebSocketProtocol
{
    private static final String GUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
    public static String buildAcceptKey(String key) throws Exception
    {
        String value = key.trim() + GUID;
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] hash = md.digest(value.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hash);
    }

    private BinaryBuffer cacheBuffer = new BinaryBuffer(10240);
    public WebSocketProtocol()
    {

    }

    /**
     0                   1                   2                   3
     0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
     +-+-+-+-+-------+-+-------------+-------------------------------+
     |F|R|R|R| opcode|M| Payload len |    Extended payload length    |
     |I|S|S|S|  (4)  |A|     (7)     |             (16/64)           |
     |N|V|V|V|       |S|             |   (if payload len==126/127)   |
     | |1|2|3|       |K|             |                               |
     +-+-+-+-+-------+-+-------------+ - - - - - - - - - - - - - - - +
     |     Extended payload length continued, if payload len == 127  |
     + - - - - - - - - - - - - - - - +-------------------------------+
     |                               |Masking-key, if MASK set to 1  |
     +-------------------------------+-------------------------------+
     | Masking-key (continued)       |          Payload Data         |
     +-------------------------------- - - - - - - - - - - - - - - - +
     :                     Payload Data continued ...                :
     + - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - +
     |                     Payload Data continued ...                |
     +---------------------------------------------------------------+
     * @param buffer
     * @return
     */
    public WebSocketFrame decode(ByteBuffer buffer)
    {
        cacheBuffer.put(buffer.array(), 0, buffer.remaining());
        if(cacheBuffer.remaining() < 2) return null;  // continue 继续读取
        cacheBuffer.mark();
        // 第一个字节FIN 和 opcode
        byte b1 = cacheBuffer.get();
        boolean fin = (b1 & 0x80) != 0;   // 0表示这个包 后面还有分片的包
        int opcode = b1 & 0x0F;           // 操作码 1 text  2 二进制 8断开连接
        // mask和长度
        byte b2 = cacheBuffer.get();
        boolean masked = (b2 & 0x80) != 0;      // 客户端的需要mask
        int payloadLen  = b2 & 0x7F;            // 这个长度不一定是最终长度
        if(!masked) throw new RuntimeException("invalid packet without mask");

        int actualLen = 0;
        if (payloadLen <= 125) {
            actualLen = payloadLen;
        }else if(payloadLen == 126){
            if(buffer.remaining() < 2)  // 说明内容不足
            {
                cacheBuffer.reset();
                return null;
            }
            actualLen = ((cacheBuffer.get() & 0xFF) << 8) | (cacheBuffer.get() & 0xFF);
        }else {  // 127
            if(buffer.remaining() < 8)  // 长度不足
            {
                cacheBuffer.reset();
                return null;
            }
            long longLen = buffer.getLong();
            if(longLen > Integer.MAX_VALUE) throw new RuntimeException("payload too long");
            actualLen = (int) longLen;
        }
        if(cacheBuffer.remaining() < actualLen + 4)  // mask 4字节
        {
            cacheBuffer.reset();
            return null;
        }
        byte[] mask = new byte[4];
        cacheBuffer.get(mask, 0, mask.length);
        byte[] payload = new byte[actualLen];
        cacheBuffer.get(payload, 0, payload.length);
        for (int i = 0; i < actualLen; i++) payload[i] ^= mask[i % 4];

        WebSocketFrame frame = new WebSocketFrame();
        frame.opcode = opcode;
        frame.bin = payload;
        frame.fin = fin;
        return frame;
    }

    public ByteBuffer encode(int opcode, byte[] data)
    {
        int len = data.length;
        int headerSize = 2;

        if (len >= 126 && len <= 65535) headerSize += 2;
        else if (len > 65535) headerSize += 8;

        ByteBuffer buffer = ByteBuffer.allocate(headerSize + len);
        buffer.put((byte) (0x80 | opcode)); // FIN + text

        if (len < 126) {
            buffer.put((byte) len);
        } else if (len <= 65535) {
            buffer.put((byte) 126);
            buffer.putShort((short) len);
        } else {
            buffer.put((byte) 127);
            buffer.putLong(len);
        }

        buffer.put(data);
        buffer.flip();
        return buffer;
    }
}
