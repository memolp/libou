import org.jeff.jsw.TemplateEngine;
import org.jeff.web.Application;
import org.jeff.web.runner.AppRunner;
import org.jeff.web.server.HttpServer;

import java.util.HashMap;
import java.util.LinkedList;

public class test
{
    public static void http_test() throws Exception
    {
        // Application 的
        Application app = new Application();
        // app.start("localhost", 8080);

        AppRunner runner = new AppRunner(app);
        runner.router.add_route("/", IndexHandler.class);
        runner.router.add_route("/home", HomeHandler.class);
        runner.router.add_static("/static", "src/test/static");
//        runner.add_static("/static", "./path");
        HttpServer server = new HttpServer(runner, "0.0.0.0", 8080);
        server.start();

        //System.in.read();
        Thread.currentThread().join();
    }

    public static void main(String[] args) throws Exception
    {
        System.out.println(System.getProperty("user.dir"));//user.dir指定了当前的路径
        http_test();
    }
}
