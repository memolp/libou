import org.jeff.template.TemplateEngine;

import java.io.IOException;
import java.util.*;

/**
 <!-- index.html -->
 <html>
 <head><title>{{ title }}</title></head>
 <body>
 {% set users = ["Alice", "Bob", "Charlie"] %}
 <ul>
 {% for i = 0; i < len(users); i++ %}
 {% if users[i] != "Bob" %}
 <li>{{ users[i] }}</li>
 {% end %}
 {% end %}
 </ul>

 {% set data = {"name": "Tom", "age": 30} %}
 <p>Name: {{ data["name"] }}</p>
 <p>Age: {{ data["age"] }}</p>
 </body>
 </html>
 */
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
