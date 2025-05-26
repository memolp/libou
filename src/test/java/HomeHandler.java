import org.jeff.template.TemplateEngine;
import org.jeff.web.Request;
import org.jeff.web.handlers.RequestHandler;
import org.jeff.web.response.Response;

import java.util.HashMap;
import java.util.LinkedList;

public class HomeHandler extends RequestHandler
{
    public void get(Request request, Response response)
    {
        response.set_header("Content-Type", "text/html; charset=UTF-8");
        HashMap<String, Object> user = new HashMap<>();
        user.put("a", "xxx");
        user.put("n", "121111");
        user.put("c", 1);

        TemplateEngine engine = new TemplateEngine();
        engine.setGlobal("Title", "异世界");
//        engine.setGlobal("users", new LinkedList<>());
//        engine.setGlobal("obj", user);
        String temp = engine.render("src/test/static/index.html");
//        String temp = engine.render("E:\\G-FLite\\test\\code\\TestCenterWeb\\WebContent\\login.html");
        response.write(temp);
    }
}