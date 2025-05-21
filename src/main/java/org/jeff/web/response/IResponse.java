package org.jeff.web.response;

/**
 * HTTP服务器响应客户端的接口类
 */
public interface IResponse
{
    /** 设置响应的HTTP版本， 默认为HTTP/1.1 */
    IResponse set_version(String version);
    /** 设置响应的状态码， 默认是200 */
    IResponse set_status(int status);
    /** 设置状态码并提供响应内容文本 */
    IResponse set_status(int status, String body);
    /** 设置响应的头字段 */
    IResponse set_header(String name, String value);
    /** 设置响应内容的格式 */
    IResponse set_contentType(String type);
    /** 设置响应内容的格式及编码 */
    IResponse set_contentType(String type, String charset);
    /** 设置响应内容的编码 */
    IResponse set_contentCharset(String charset);
    /** 写入响应的内容 */
    IResponse write(String text) ;
    /** 向客户端发送一个文件 */
    IResponse write(FileWriter fileWriter);
    /** 向客户端发送文件，文件将以chunked形式送出 */
    IResponse write(FileChunkWriter fileChunkWriter);
    /** 向客户端发送文件， 文件以Range格式响应客户端 */
    IResponse write(FileRangeWriter fileRangeWriter);
    /** 设置响应的cookie */
    IResponse set_cookie(String name, String value);
    IResponse set_cookie(String name, String value, String expires);
    IResponse set_cookie(String name, String value, int max_age);
    IResponse set_cookie(String name, String value, String path, String expires);
    IResponse set_cookie(String name, String value, String path, int max_age);
    IResponse set_cookie(String name, String value, String path, String domain, String expires);
    IResponse set_cookie(String name, String value, String path, String domain, int max_age);
}
