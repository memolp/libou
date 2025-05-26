import org.jeff.jsw.JsEngine;
import org.jeff.jsw.JsInterpreter;
import org.jeff.template.TemplateEngine;

import java.io.IOException;
import java.util.*;

public class jswtest
{
    public static void main(String[] args) throws IOException {
        TemplateEngine engine = new TemplateEngine();
        engine.setGlobal("title", "Advanced Template Engine");

        List<String> users = new ArrayList<>(Arrays.asList("Alice", "Bob", "Charlie"));
        engine.setGlobal("users", users);

        Map<String, Object> data = new HashMap<>();
        data.put("name", "T2om");
        data.put("age", 30);
        engine.setGlobal("data", data);

        String result = engine.render("src/test/scripts/index.html");
        System.out.println(result);
    }
}
