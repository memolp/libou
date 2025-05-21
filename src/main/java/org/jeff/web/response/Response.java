package org.jeff.web.response;

import java.io.IOException;
import java.util.HashMap;

public class Response implements IResponse
{
    public String version = "HTTP/1.1";
    public ResponseStatus responseStatus = ResponseStatus.OK;
    public HashMap<String, String> headers = new HashMap<>();
    public HashMap<String, String> cookies = new HashMap<>();

    public boolean onlyHeader = false;
    public boolean close_connection = false;

    protected IBodyWriter mBodyWriter = null;

    public Response()
    {

    }

    public Response(int status)
    {
        this.set_status(status);
    }

    public IResponse set_version(String version)
    {
        this.version = version;
        return this;
    }

    public IResponse set_status(int status)
    {
        this.responseStatus = ResponseStatus.fromCode(status);
        return this;
    }

    public IResponse set_status(int status, String body)
    {
        this.responseStatus = ResponseStatus.fromCode(status);
        this.write(body);
        return this;
    }

    public IResponse set_header(String name, String value)
    {
        this.headers.put(name, value);
        return this;
    }

    @Override
    public IResponse set_contentType(String type) {
        return null;
    }

    @Override
    public IResponse set_contentType(String type, String charset) {
        return null;
    }

    @Override
    public IResponse set_contentCharset(String charset) {
        return null;
    }

    public IResponse only_header(boolean v)
    {
        this.onlyHeader = v;
        return this;
    }

    public IResponse write(String text)
    {
        if(this.mBodyWriter == null)
        {
            this.mBodyWriter = new TextWriter();
        }
        try {
            this.mBodyWriter.write(text);
        }catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        return this;
    }

    @Override
    public IResponse write(FileWriter fileWriter)
    {
        this.mBodyWriter = fileWriter;
        return this;
    }

    @Override
    public IResponse write(FileChunkWriter fileChunkWriter)
    {
        this.mBodyWriter = fileChunkWriter;
        return this;
    }

    @Override
    public IResponse write(FileRangeWriter fileRangeWriter)
    {
        this.mBodyWriter = fileRangeWriter;
        return this;
    }

    /**
     * 设置Cookie
     * @param name
     * @param value
     */
    public IResponse set_cookie(String name, String value)
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
    public IResponse set_cookie(String name, String value, String expires)
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
    public IResponse set_cookie(String name, String value, int max_age)
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
    public IResponse set_cookie(String name, String value, String path, String expires)
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
    public IResponse set_cookie(String name, String value, String path, int max_age)
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
    public IResponse set_cookie(String name, String value, String path, String domain, String expires)
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
    public IResponse set_cookie(String name, String value, String path, String domain, int max_age)
    {
        String cookie = String.format("%s=%s; Path=%s; Domain=%s; Max-Age=%d", name, value, path, domain, max_age);
        this.cookies.put(name, cookie);
        return this;
    }
}
