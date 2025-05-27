package org.jeff.template;

import org.jeff.template.exprs.Callable;
import org.jeff.template.nodes.BlockNode;
import org.jeff.template.nodes.CodeNode;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TemplateEngine
{
    /** 提供给模板语言的函数类 */
    class FunLen implements Callable
    {

        @Override
        public Object call(RenderContext context, Object... args)
        {
            if(args.length != 1) throw new RuntimeException("len function only support one param");
            Object itr = args[0];
            if(itr instanceof String)
                return ((String)itr).length();
            else if(itr instanceof List)
            {
                return ((List<?>)itr).size();
            }else if(itr instanceof Map)
            {
                return ((Map<?,?>)itr).size();
            }
            throw new RuntimeException("len function unexpected symbols " + itr);
        }
    }
    class FunPairs implements Callable
    {
        @Override
        public Object call(RenderContext context, Object... args)
        {
            if(args.length != 1) throw new RuntimeException("pairs function only support one param");
            Object itr = args[0];
            if(itr instanceof Iterable<?>) return itr;
            if(itr instanceof Map<?,?>) return ((Map<?,?>)itr).keySet();
            throw new RuntimeException("pairs function unexpected symbols " + itr);
        }
    }
    private final Map<String, Object> _globals = new HashMap<>();
    /** 缓存模板 */
    private static final Map<String, TemplateCode> TempCache = new ConcurrentHashMap<>();

    public TemplateEngine(){    }
    /**
     * 设置给模板语言中使用的变量和其值
     * @param name
     * @param value
     */
    public void setGlobal(String name, Object value)
    {
        _globals.put(name, value);
    }

    /**
     * 渲染模板
     * @param file
     * @return
     */
    public String render(String file)
    {
        CodeNode code = readCode(file);
        _globals.put("len", new FunLen());
        _globals.put("pairs", new FunPairs());
        RenderContext context = new RenderContext(_globals);
        code.render(context);
        return context.getStdout();
    }

    private CodeNode readCode(String file)
    {
        File f = new File(file);
        TemplateCode temp = TempCache.getOrDefault(file, null);
        if(temp != null)
        {
            if(temp.lastModified == f.lastModified())
            {
                return temp.code;
            }
        }
        try {
            List<String> content = Files.readAllLines(Paths.get(file), StandardCharsets.UTF_8);
            CodeNode code = TemplateParser.parse(content);
            if(temp == null)
            {
                temp = new TemplateCode();
                temp.code = code;
                temp.lastModified = f.lastModified();
                TempCache.put(file, temp);
            }else
            {
                temp.code = code;
                temp.lastModified = f.lastModified();
            }
            return code;
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    class TemplateCode
    {
        public long lastModified = 0;
        public CodeNode code = null;
    }

}
