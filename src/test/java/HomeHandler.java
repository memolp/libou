import org.jeff.jsw.TemplateEngine;
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
        HashMap<String, Object> vars = new HashMap<>();
        vars.put("Title", "异世界");
        vars.put("obj", user);
        vars.put("users", new LinkedList<>());

        TemplateEngine engine = new TemplateEngine();
        String temp = engine.render("src/test/static/index.html", vars);
        response.write(temp);
    }
}