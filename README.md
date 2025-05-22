# libou
一个简单的Java版本的HTTP服务，网络采用AIO模型。内部写了一个模板脚本语言还没完全完成。<br>

# 快速开始
### 创建请求处理类
```java
// 请求处理类 注意不能作为内部类
public class IndexHandler extends RequestHandler
{
    @Override
    public void get(Request request, Response response)
    {
        response.write("Hello, World!");
    }
}
```
### 启动HTTP服务
```java
public static void main(String[] args) throws Exception
{
    Application app = new Application();
    app.router.add_route("/", IndexHandler.class);  // http://127.0.0.1:8080/ => 自动使用IndexHandler处理
    app.router.add_static("/static/", "src/test/static");  // http://127.0.0.1:8080/static/xxx 相关的静态资源请求
    app.start("0.0.0.0", 8080);
}
```

# 性能测试
客户端Channel未设置TCP_NODELAY
```shell
./wrk -c 10 -t 2 -d 60 http://127.0.0.1/
Running 1m test @ http://127.0.0.1/
  2 threads and 10 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    40.89ms    1.23ms  56.83ms   94.12%
    Req/Sec   122.33     13.32   151.00     69.98%
  14642 requests in 1.00m, 2.15MB read
Requests/sec:    243.70
Transfer/sec:     36.67KB
```
正常设置TCP_NODELAY后，并且时keepAlive保持连接状态下。
1. TPS 可以达到1.4万每秒
2. 对于动态网站这种可以支持keep-alive的还是可以。
```shell
./wrk -c 10 -t 2 -d 60 http://172.0.0.1/
Running 1m test @ http://127.0.0.1/
  2 threads and 10 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     1.88ms    3.22ms  39.35ms   83.99%
    Req/Sec     6.97k   485.59     8.23k    72.82%
  833723 requests in 1.00m, 122.45MB read
Requests/sec:  13875.33
Transfer/sec:      2.04MB
```
正常设置TCP_NODELAY后，每次请求都重新建立连接（keepAlive=false）<br>
1. 说明每次建立连接会很耗时。3000tips/每秒
2. 很多场景下都无法利用keep-alive，这样其实TPS并不是特别高。
3. 以上的全部测试网络线程数开的是4个，也是4个线程去处理请求。
```shell
./wrk -c 10 -t 2 -d 60http://172.0.0.1/
Running 1m test @ http://172.0.0.1/
  2 threads and 10 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     1.71ms    3.20ms  50.08ms   86.89%
    Req/Sec     1.68k   151.31     2.27k    84.72%
  200616 requests in 1.00m, 32.14MB read
Requests/sec:   3342.29
Transfer/sec:    548.35KB
```