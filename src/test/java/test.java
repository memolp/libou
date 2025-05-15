import org.jeff.jsw.BuiltinFunction;
import org.jeff.jsw.Env;
import org.jeff.jsw.Parser;
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

    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");
        String code = "echo(\"<ul style='padding:0'>\");\n" +
                "            for(let i in a)\n" +
                "            {\n" +
                "                if(i == 2)\n" +
                "                {\n" +
                "                    echo('<li><span style=\"width:%spx; height:%s%%\"><span></li>', i, 50);\n" +
                "                }else\n" +
                "                {\n" +
                "                    echo(\"<li><span> xx</span></li>\");\n" +
                "                }\n" +
                "            }\n" +
                "            echo(\"</ul>\");";

        Tokenizer s = new Tokenizer(code);
//        for(Token t :  s.tokenize())
//        {
//            System.out.println(t.toString());
//        }
        Parser parser = new Parser(s.tokenize());
        Statement statement = parser.parseStatements();
        {
            System.out.println(statement);
        }
        Env evn = new Env();
        List<Integer> a = new ArrayList<>();
        a.add(1);
        a.add(2);
        a.add(5);
        a.add(4);

        evn.set("a", a);
        evn.set("echo", new BuiltinFunction() {
            @Override
            public Object call(Env env, Object... args)
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
        });
        statement.execute(evn);
    }
}
