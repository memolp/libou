package org.jeff.web;

import org.jeff.web.router.RouterChooser;

public class HttpContext
{
    /** 路由管理器-用于决定请求地址使用哪个路由处理 */
    public RouterChooser chooser;
    /** 应用对象 */
    public Application application;
    /** 内部的文件写入Buffer大小 */
    public int fileBuffSize = 1024 * 1024 * 5;
    /** 内部的请求读取buffer大小 */
    public int readBufferSize = 1024 * 10;
    /** 服务器是否支持长连接 */
    public boolean keepAlive = true;
    /** 静态资源是否支持缓存 */
    public boolean supportCache = true;
    /** 静态资源缓存的过期时间单位秒 */
    public int cacheMaxAge = 3600;
    /** 用于告诉客户端服务器的名字 */
    public String serverName = "Libou/1.1";
    /** 网络线程最大支持的梳理 */
    public int maxThreadNum = 4;
}
