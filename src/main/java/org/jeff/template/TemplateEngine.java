package org.jeff.template;

import org.jeff.template.exprs.Callable;
import org.jeff.template.nodes.NodeBlock;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TemplateEngine
{
    class FunLen implements Callable
    {

        @Override
        public Object call(RenderContext context, Object... args)
        {
            if(args.length != 1) throw new RuntimeException("sss");
            Object itr = args[0];
            if(itr instanceof String)
                return ((String)itr).length();
            else if(itr instanceof List)
            {
                return ((List)itr).size();
            }else if(itr instanceof Map)
            {
                return ((Map)itr).size();
            }
            throw new RuntimeException("sss");
        }
    }
    private final Map<String, Object> _globals = new HashMap<>();

    public TemplateEngine(){    }

    public void setGlobal(String name, Object value)
    {
        _globals.put(name, value);
    }

    public String render(String file) throws IOException
    {
        String content = new String(Files.readAllBytes(Paths.get(file)),  StandardCharsets.UTF_8);
        NodeBlock code = TemplateParser.parse(content);
        _globals.put("len", new FunLen());
        RenderContext context = new RenderContext(_globals);
//        return TemplateInterpreter.render(nodes, context);
        code.render(context);
        System.out.println(context.getStdout());
//        System.out.println(code);
        return "";
    }


}
