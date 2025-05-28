package org.jeff.web.response;

import org.jeff.web.HttpContext;
import org.jeff.web.utils.HttpDateHelper;
import org.jeff.web.ws.WebSocketMessage;

import java.nio.ByteBuffer;

/**
 * 真正创建的Web响应对象，不提供给外部业务，仅内部使用
 */
public class InternalResponse extends Response
{
    /** 标记是否使用websocket */
    public boolean useWebSocket = false;
    /** 升级websocket需要提供消息处理函数 */
    public WebSocketMessage listener = null;

    public InternalResponse()
    {
    }

    /**
     * 构建HTTP的请求头内容
     * @param context
     * @return
     */
    public String build_header(HttpContext context)
    {
        this.onBeforeWriteHeader(context);
        StringBuilder header = new StringBuilder();
//        header.append(this.version).append(" ").append(this.responseStatus.code()).append(" ").append(this.responseStatus.reason()).append("\r\n");
        header.append(String.format("%s %d %s\r\n", this.version, this.responseStatus.code(), this.responseStatus.reason()));
        for(String key : this.headers.keySet())
        {
            String value = this.headers.get(key);
            header.append(String.format("%s: %s\r\n", key, value));
//            header.append(key).append(": ").append(value).append("\r\n");
        }
        if(!this.headers.containsKey("Content-Type"))
        {
            header.append("Content-Type: text/plain; charset=UTF-8\r\n");
        }
        for(String key : this.cookies.keySet())
        {
            header.append(String.format("Set-Cookie: %s\r\n", this.cookies.get(key)));
//            header.append("Set-Cookie: ").append(this.cookies.get(key)).append("\r\n");
        }
        header.append("\r\n");
        return header.toString();
    }

    /**
     * 在发送头前提供一个额外的处理接口
     * @param context
     */
    protected void onBeforeWriteHeader(HttpContext context)
    {
        if(this.mBodyWriter == null)
        {
            this.set_header("Content-Length", "0");
            return;
        }
        this.mBodyWriter.onBeforeWriteHeader(this, context);
    }

    /**
     * 写入响应body的时候，为了支持异步的分块传输，会每写完一次调用一次读取下一份数据，直到完全没有
     * 真正的逻辑是不同BodyWriter自身的实现
     * @return
     */
    public ByteBuffer nextChunk()
    {
        if(this.mBodyWriter == null) return null;
        return this.mBodyWriter.nextChunk();
    }

    /**
     * 判断是否有下一份数据需要发送
     * @return
     */
    public boolean hasNext()
    {
        if(this.mBodyWriter == null) return false;
        return this.mBodyWriter.hasNext();
    }

    /**
     * 升级为Websocket的接口
     * 目前的操作不是特别完美，但目前想不到好办法
     * @param listener
     */
    public void upgradeWebSocket(WebSocketMessage listener)
    {
        this.useWebSocket = true;
        this.listener = listener;
    }
}
