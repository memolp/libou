import org.jeff.web.Application;
import org.jeff.web.runner.AppRunner;
import org.jeff.web.server.HttpServer;

public class test
{

    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");
        // Application 的
        Application app = new Application();
        //app.add_route("/", IndexHandler.class);
        //app.start("localhost", 8080);

        AppRunner runner = new AppRunner(app);
        runner.router.add_route("/", IndexHandler.class);
        runner.router.add_route("/home", HomeHandler.class);
        runner.router.add_static("/static", "../static");
//        runner.add_static("/static", "./path");
        HttpServer server = new HttpServer(runner, "localhost", 8080);
        server.start();

        //System.in.read();
        Thread.currentThread().join();
    }
}
