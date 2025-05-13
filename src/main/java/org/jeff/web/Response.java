package org.jeff.web;

import java.util.HashMap;

public class Response
{
    public String version = "HTTP/1.1";
    public int status = 200;
    public String msg = "OK";
    public StringBuffer body = new StringBuffer();
    public HashMap<String, String> headers = new HashMap<>();
    public HashMap<String, String> cookies = new HashMap<>();

    public Response()
    {

    }

    public Response(int status, String msg, String body)
    {
        this.status = status;
        this.msg = msg;
        this.body.append(body);
    }

    public void set_version(String version)
    {
        this.version = version;
    }

    public void set_status(int status, String msg, String body)
    {
        this.status = status;
        this.msg = msg;
        this.body.append(body);
    }

    public void set_header(String name, String value)
    {
        this.headers.put(name, value);
    }

    public void write(String body)
    {
        this.body.append(body);
    }
    /**
     * 设置Cookie
     * @param name
     * @param value
     */
    public void set_cookie(String name, String value)
    {
        String cookie = String.format("%s=%s", name, value);
        this.cookies.put(name, cookie);
    }

    /**
     * 设置带过期时间的Cookie
     * @param name
     * @param value
     * @param expires 过期时间，标准时间rfc822格式:Wed, 21 Oct 2015 07:28:00 GMT
     */
    public void set_cookie(String name, String value, String expires)
    {
        String cookie = String.format("%s=%s; Expires=%s", name, value, expires);
        this.cookies.put(name, cookie);
    }

    /**
     * 设置带过期时间的Cookie
     * @param name
     * @param value
     * @param max_age 单位秒 Max-Age的优先级高于Expires
     */
    public void set_cookie(String name, String value, int max_age)
    {
        String cookie = String.format("%s=%s; Max-Age=%d", name, value, max_age);
        this.cookies.put(name, cookie);
    }

    /**
     * 设置带有效路径和过期时间的Cookie
     * @param name
     * @param value
     * @param path  以正斜杠（/）分割 如果Path=/docs 那么 /docs 、 /docs/web 、/docs/web/http 都满足需求
     * @param expires  过期时间
     */
    public void set_cookie(String name, String value, String path, String expires)
    {
        String cookie = String.format("%s=%s; Path=%s; Expires=%s", name, value, path, expires);
        this.cookies.put(name, cookie);
    }

    /**
     * 设置带有效路径和过期时间的Cookie
     * @param name
     * @param value
     * @param path
     * @param max_age
     */
    public void set_cookie(String name, String value, String path, int max_age)
    {
        String cookie = String.format("%s=%s; Path=%s; Max-Age=%d", name, value, path, max_age);
        this.cookies.put(name, cookie);
    }

    /**
     * 支持指定Cookie可送达的主机
     * @param name
     * @param value
     * @param path
     * @param domain
     * @param expires
     */
    public void set_cookie(String name, String value, String path, String domain, String expires)
    {
        String cookie = String.format("%s=%s; Domain=%s; Path=%s; Expires=%s", name, value, domain, path, expires);
        this.cookies.put(name, cookie);
    }

    /**
     * 支持指定Cookie可送达的主机
     * @param name
     * @param value
     * @param path
     * @param domain
     * @param max_age
     */
    public void set_cookie(String name, String value, String path, String domain, int max_age)
    {
        String cookie = String.format("%s=%s; Path=%s; Domain=%s; Max-Age=%d", name, value, path, domain, max_age);
        this.cookies.put(name, cookie);
    }


}
