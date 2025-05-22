package org.jeff.web.server;

import org.jeff.web.HttpContext;
import org.jeff.web.Request;
import org.jeff.web.parse.RequestParser;
import org.jeff.web.parse.RequestParserState;
import org.jeff.web.response.*;
import org.jeff.web.utils.HttpDateHelper;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class Session
{
    protected final AsynchronousSocketChannel client;
    protected final SessionReadHandler readHandler = new SessionReadHandler();
    protected final HttpContext context;
    protected final ByteBuffer readBuffer;
    protected RequestParser parser = null;
    protected boolean close_connection = false;

    public Session(HttpContext context, AsynchronousSocketChannel client)
    {
        this.context = context;
        this.client = client;
        this.readBuffer = ByteBuffer.allocate(context.readBufferSize);
    }
    /** Http请求解析开始，边读取边解析 */
    public void doHandle()
    {
        this.parser = new RequestParser();
        this.close_connection = false;
        this.doRead();
    }
    /** 投递读取事件 */
    private void doRead()
    {
        if(!this.client.isOpen()) return;
        this.client.read(this.readBuffer, this, this.readHandler);
    }
    /** 读取到内容 */
    public void onRead(int size)
    {
        if(size <= 0)
        {
            this.doClose();
            return;
        }
        this.readBuffer.flip();
        try
        {
            RequestParserState state = this.parser.parser(this.readBuffer);
            if(state == RequestParserState.Continue)
            {
                this.readBuffer.clear();
                this.doRead();
                return;
            }
            this.onRequest(this.parser.request, new InternalResponse());
        } catch (Exception e)
        {
            this.doClose();
        }
    }
    /** 读取失败 */
    public void onReadError(Throwable e)
    {
        this.doClose();
    }
    /** 执行客户端请求 */
    private void onRequest(Request request, InternalResponse response)
    {
        try
        {
            // 如果客户端请求中声明要close，那么就close；但是如果服务器本身设置了不保持连接，那么也close
            String keepAlive = request.get_header("Connection");
            if(keepAlive != null && keepAlive.toLowerCase().contains("close"))
                this.close_connection = true;
            if(!this.close_connection && !this.context.keepAlive)
                this.close_connection = true;
            request.RemoteAddress = this.client.getRemoteAddress().toString();
            // 将请求交给路由筛选器处理
            this.context.chooser.doRequest(this.context, request, response);
            // 处理结束后发送响应结果
            this.sendResponse(response);
        }catch (Exception e)
        {
            this.doClose();
        }
    }
    /** 关闭这条连接 */
    protected void doClose()
    {
        try
        {
            this.client.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /** 在response发送前的处理 */
    protected void beforeSendResponse(InternalResponse response)
    {
        // 设置一下本次请求是否保持连接
        if(!this.close_connection)
            response.set_header("Connection", "keep-alive");
        else
            response.set_header("Connection", "close");
        response.set_header("Server", this.context.serverName);     // 设置服务器名字
        response.set_header("Date", HttpDateHelper.formatToRFC822());  // 设置当前时间
    }
    /** 发送响应结果给客户端 */
    protected void sendResponse(InternalResponse response)
    {
        this.beforeSendResponse(response);  // 在发送请求前，Session有哪些头需要设置
        // 开始正式发送响应的数据
        WriteHeaderHandler handler = new WriteHeaderHandler(this, response, new WriteBodyHandler(this, response));
        handler.doSendHeader(this.context);
    }
    /** 用于异步写入数据 */
    public <A> void write(ByteBuffer buffer, A attachment, CompletionHandler<Integer, A> handler)
    {
        this.client.write(buffer, attachment, handler);
    }
    /** 请求完成后，当前连接是继续服务还是关闭 */
    public void onResponseCompleted()
    {
        if(this.close_connection)
            this.doClose();
        else
            this.doHandle();
    }
    /** 请求失败了。直接关闭连接 */
    public void onResponseFailed(Throwable e)
    {
        this.doClose();
    }
}
