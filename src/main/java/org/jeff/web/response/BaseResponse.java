package org.jeff.web.response;

import org.jeff.web.HttpDateHelper;

import java.nio.ByteBuffer;
import java.util.HashMap;

public class BaseResponse implements InternalResponse, Response
{
    public String version = "HTTP/1.1";
    public ResponseStatus responseStatus = ResponseStatus.OK;
    public StringBuffer body = new StringBuffer();
    public HashMap<String, String> headers = new HashMap<>();
    public HashMap<String, String> cookies = new HashMap<>();
    public boolean onlyHeader = false;
    public boolean close_connection = false;
    private boolean send_body = false;

    public BaseResponse()
    {

    }

    public BaseResponse(int status)
    {
        this.set_status(status);
    }

    public Response set_version(String version)
    {
        this.version = version;
        return this;
    }

    public Response set_status(int status)
    {
        this.responseStatus = ResponseStatus.fromCode(status);
        return this;
    }

    public Response set_status(int status, String body)
    {
        this.responseStatus = ResponseStatus.fromCode(status);
        this.body.append(body);
        return this;
    }

    public Response set_header(String name, String value)
    {
        this.headers.put(name, value);
        return this;
    }

    public Response only_header(boolean v)
    {
        this.onlyHeader = v;
        return this;
    }

    public Response write(String text)
    {
        this.body.append(text);
        return this;
    }
    /**
     * 设置Cookie
     * @param name
     * @param value
     */
    public Response set_cookie(String name, String value)
    {
        String cookie = String.format("%s=%s", name, value);
        this.cookies.put(name, cookie);
        return this;
    }

    /**
     * 设置带过期时间的Cookie
     * @param name
     * @param value
     * @param expires 过期时间，标准时间rfc822格式:Wed, 21 Oct 2015 07:28:00 GMT
     */
    public Response set_cookie(String name, String value, String expires)
    {
        String cookie = String.format("%s=%s; Expires=%s", name, value, expires);
        this.cookies.put(name, cookie);
        return this;
    }

    /**
     * 设置带过期时间的Cookie
     * @param name
     * @param value
     * @param max_age 单位秒 Max-Age的优先级高于Expires
     */
    public Response set_cookie(String name, String value, int max_age)
    {
        String cookie = String.format("%s=%s; Max-Age=%d", name, value, max_age);
        this.cookies.put(name, cookie);
        return this;
    }

    /**
     * 设置带有效路径和过期时间的Cookie
     * @param name
     * @param value
     * @param path  以正斜杠（/）分割 如果Path=/docs 那么 /docs 、 /docs/web 、/docs/web/http 都满足需求
     * @param expires  过期时间
     */
    public Response set_cookie(String name, String value, String path, String expires)
    {
        String cookie = String.format("%s=%s; Path=%s; Expires=%s", name, value, path, expires);
        this.cookies.put(name, cookie);
        return this;
    }

    /**
     * 设置带有效路径和过期时间的Cookie
     * @param name
     * @param value
     * @param path
     * @param max_age
     */
    public Response set_cookie(String name, String value, String path, int max_age)
    {
        String cookie = String.format("%s=%s; Path=%s; Max-Age=%d", name, value, path, max_age);
        this.cookies.put(name, cookie);
        return this;
    }

    /**
     * 支持指定Cookie可送达的主机
     * @param name
     * @param value
     * @param path
     * @param domain
     * @param expires
     */
    public Response set_cookie(String name, String value, String path, String domain, String expires)
    {
        String cookie = String.format("%s=%s; Domain=%s; Path=%s; Expires=%s", name, value, domain, path, expires);
        this.cookies.put(name, cookie);
        return this;
    }

    /**
     * 支持指定Cookie可送达的主机
     * @param name
     * @param value
     * @param path
     * @param domain
     * @param max_age
     */
    public Response set_cookie(String name, String value, String path, String domain, int max_age)
    {
        String cookie = String.format("%s=%s; Path=%s; Domain=%s; Max-Age=%d", name, value, path, domain, max_age);
        this.cookies.put(name, cookie);
        return this;
    }

    public String build_header()
    {
        this.onBeforeWriteHeader();
        StringBuilder header = new StringBuilder();
        header.append(String.format("%s %s\r\n", this.version, this.responseStatus));
        for(String key : this.headers.keySet())
        {
            String value = this.headers.get(key);
            header.append(String.format("%s: %s\r\n", key, value));
        }
        if(!this.headers.containsKey("Connection"))
        {
            if(this.close_connection)
                header.append("Connection: close\r\n");
            else
                header.append("Connection: keep-alive\r\n");
        }
        if(!this.headers.containsKey("Content-Type"))
        {
            header.append("Content-Type: text/plain; charset=UTF-8\r\n");
        }
        if(!this.headers.containsKey("Date"))
        {
            header.append(String.format("Date: %s\r\n", HttpDateHelper.formatToRFC822()));
        }
        header.append("\r\n");
        return header.toString();
    }

    public void onBeforeWriteHeader()
    {
        // TODO 实际上与HTTP交互的长度是字节的长度，如果直接用this.body.length()，如果出现中文就长度不对了
        int length = 0;
        if(!onlyHeader)
        {
            length = this.body.toString().getBytes().length;
        }
        if(!this.headers.containsKey("Content-Length"))
        {
            this.set_header("Content-Length", String.format("%d", length));
        }
    }

    public ByteBuffer next_trunk()
    {
        if(send_body) return null;
        send_body = true;
        if(this.body.length() < 1) return null;
        return ByteBuffer.wrap(this.body.toString().getBytes());
    }

    @Override
    public boolean include_body()
    {
        return !this.onlyHeader;
    }

    @Override
    public boolean keepAlive()
    {
        return !this.close_connection;
    }
}
