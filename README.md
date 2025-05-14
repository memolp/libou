# libou


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
正常设置TCP_NODELAY后
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