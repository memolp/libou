import org.jeff.jsw.TemplateEngine;
import org.jeff.web.Application;
import org.jeff.web.server.HttpServer;

import java.util.HashMap;
import java.util.LinkedList;

public class test
{
    public static void http_test() throws Exception
    {
        // Application 的
        Application app = new Application();
        app.router.add_route("/", IndexHandler.class);
        app.router.add_route("/home", HomeHandler.class);
        app.router.add_static("/static", "src/test/static");
        app.router.add_route("/oc", FileDownloadHandler.class);
        app.start("0.0.0.0", 8080);

        //HttpServer server = new HttpServer(app, "0.0.0.0", 8080);
        //server.start();
        //System.in.read();

    }

    public static void main(String[] args) throws Exception
    {
        System.out.println(System.getProperty("user.dir"));//user.dir指定了当前的路径
        http_test();
    }
}
