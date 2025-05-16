import org.jeff.jsw.JsContext;
import org.jeff.jsw.JsEngine;
import org.jeff.jsw.statements.ASTParser;
import org.jeff.jsw.objs.JsBuiltinFunction;
import org.jeff.jsw.objs.JsObject;
import org.jeff.jsw.statements.Statement;
import org.jeff.jsw.tokens.Tokenizer;
import org.jeff.web.Application;
import org.jeff.web.runner.AppRunner;
import org.jeff.web.server.HttpServer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class test
{
    static void http_test() throws Exception
    {
        // Application 的
        Application app = new Application();
        //app.add_route("/", IndexHandler.class);
        //app.start("localhost", 8080);

        AppRunner runner = new AppRunner(app);
        runner.router.add_route("/", IndexHandler.class);
        runner.router.add_route("/home", HomeHandler.class);
        runner.router.add_static("/static", "E:\\JeffProject\\JavaProjects\\libou\\src\\test\\static");
//        runner.add_static("/static", "./path");
        HttpServer server = new HttpServer(runner, "0.0.0.0", 8080);
        server.start();

        //System.in.read();
        Thread.currentThread().join();
    }

    class echoFunction extends JsBuiltinFunction
    {
        @Override
        public JsObject call(JsContext jsContext, JsObject... args)
        {
            if(args.length == 1)
                System.out.println(args[0]);
            else
            {
                String fmt = String.valueOf(args[0]);
                List<Object> _args = new LinkedList<>();
                for(int i = 1; i < args.length; i++)
                {
                    _args.add(args[i]);
                }
                System.out.printf(fmt, _args.toArray());
            }
            return null;
        }
    }

    public static void main(String[] args) throws Exception
    {
        System.out.println(System.getProperty("user.dir"));//user.dir指定了当前的路径
        JsEngine engine = new JsEngine();
        engine.doFile("src/test/scripts/test.jsw");
    }
}
