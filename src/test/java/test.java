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

    public static void main(String[] args)
    {
        System.out.println(System.getProperty("user.dir"));//user.dir指定了当前的路径
        //JsEngine engine = new JsEngine();
        //engine.doFile("src/test/scripts/test.jsw");
//        UserObject user = new UserObject();
        HashMap<String, Object> user = new HashMap<>();
        user.put("a", "xxx");
        user.put("n", "121111");
        user.put("c", 1);
        HashMap<String, Object> vars = new HashMap<>();
        vars.put("Title", "异世界");
        vars.put("obj", user);
        vars.put("users", new LinkedList<>());
        String content = TemplateEngine.render("src/test/static/index.html", vars);
        System.out.println(content);
    }
}
