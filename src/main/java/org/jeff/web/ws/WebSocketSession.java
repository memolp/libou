package org.jeff.web.ws;

import org.jeff.core.BinaryBuffer;
import org.jeff.web.HttpContext;
import org.jeff.web.server.Session;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 升级后的Websocket 会话对象
 */
public class WebSocketSession extends Session
{
    private final WebSocketProtocol protocol;
    private final WebSocketMessage listener;
    private final BinaryBuffer fragmentCache = new BinaryBuffer(10240);
    private int fragmentOpcode = -1;
    private final Queue<ByteBuffer> writeQueue = new LinkedList<>();
    private final AtomicBoolean isWriting = new AtomicBoolean(false);
    public final String SessionID = UUID.randomUUID().toString();

    public WebSocketSession(HttpContext context, AsynchronousSocketChannel client, WebSocketMessage listener)
    {
        super(context, client);
        this.protocol = new WebSocketProtocol();
        this.listener = listener;
    }

    public void doHandle()
    {
        this.listener.onConnected(this);
        this.doRead();
    }

    @Override
    public void onRead(int size)
    {
        if(size <= 0)
        {
            this.doClose();
            return;
        }
        this.readBuffer.flip();
        WebSocketFrame frame = protocol.decode(this.readBuffer);
        if(frame == null)
        {
            this.doRead();
            return;
        }
        this.handleFrame(frame);
    }
    private void handleFrame(WebSocketFrame frame)
    {
        switch (frame.opcode)
        {
            case 1: case 2:   // 1是文本， 2是二进制
                if(!frame.fin)  // 如果指定了opcode的类型，但是fin为0，说明是第一个分帧的包
                {
                    this.fragmentOpcode = frame.opcode;
                    this.fragmentCache.clear();
                    this.fragmentCache.put(frame.bin, 0, frame.bin.length);
                    break;
                }
                // 如果fin = 1 说明是一个完整的包
                this.listener.onMessage(this, frame.opcode, fragmentCache.array());
                break;
            case 0:  // 后面的分帧数据包。 第一个包的opcode是1或者2 后面继续发的opcode就为0
                if(this.fragmentOpcode == -1)
                {
                    this.doClose();
                    return;
                }
                this.fragmentCache.put(frame.bin, 0, frame.bin.length);
                if(frame.fin)  // 说明这个已经是最后一个了
                {
                    this.listener.onMessage(this, this.fragmentOpcode, this.fragmentCache.array());
                    this.fragmentOpcode = -1;
                    this.fragmentCache.clear();
                }
                break;
            case 8:  // 网络关闭
                this.doClose();
                return;
            case 9:  // ping
                this.SendPong(frame.bin);
                break;
            case 10: // pong 发ping的回报pong 直接忽略即可
                break;
            default:
                this.doClose();
                return;
        }
        this.doRead();
    }

    /**
     * 发送二进制包
     * @param data
     */
    public void Send(byte[] data)
    {
        this.Send(data, 2);
    }

    /**
     * 可选类型，支持发文本或者二进制
     * @param data
     * @param opcode
     */
    public void Send(byte[] data, int opcode)
    {
        ByteBuffer buffer = this.protocol.encode(opcode, data);
        this.addWriteQueue(buffer);
    }

    protected void SendPong(byte[] ping)
    {
        ByteBuffer buffer = this.protocol.encode(0xA, ping);
        this.addWriteQueue(buffer);
    }

    protected void addWriteQueue(ByteBuffer buffer)
    {
        synchronized (writeQueue)  // TODO 如果阻塞了太多包就需要做特殊处理
        {
            writeQueue.offer(buffer);
            if(isWriting.get()) return;
            isWriting.set(true);
            this.doWrite();
        }
    }

    protected void doWrite()
    {
        synchronized (writeQueue)
        {
            ByteBuffer buffer = this.writeQueue.poll();
            if(buffer == null)
            {
                isWriting.set(false);
                return;
            }
            this.write(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
                @Override
                public void completed(Integer result, ByteBuffer attachment) {
                    if (attachment.hasRemaining()) write(attachment, attachment, this);
                    else doWrite();
                }
                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {
                    doClose();
                }
            });
        }
    }

    @Override
    protected void doRead()
    {
        this.readBuffer.clear();
        super.doRead();
    }

    @Override
    protected void doClose()
    {
        this.listener.onDisconnected(this);
        super.doClose();
        this.fragmentCache.clear();
        this.fragmentOpcode = -1;
        this.writeQueue.clear();
        this.isWriting.set(false);
    }
}
