package org.jeff.template;

import org.jeff.template.exprs.Callable;
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

    /**
     * 用于将list map 转换成可迭代的对象提供给模板语言中for迭代使用
     */
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
    /** 全局使用的环境变量 */
    private static final Map<String, Object> _globals = new HashMap<>();
    {
        _globals.put("len", new FunLen());
        _globals.put("pairs", new FunPairs());
    }
    /**
     * 缓存模板
     * 目前使用ConcurrentHashMap来做线程安全读取，
     * 也可以使用HashMap来做，会有一点点性能提升，但是放弃了线程安全。
     */
    private static final Map<String, TemplateCode> TempCache = new ConcurrentHashMap<>();
    /** 当前渲染的上下文 */
    private final RenderContext _context = new RenderContext(_globals);
    /** 模板引擎 */
    public TemplateEngine(){    }
    /**
     * 设置给模板语言中使用的变量和其值
     * @param name
     * @param value
     */
    public void setGlobal(String name, Object value)
    {
        _context.set(name, value);
    }
    /**
     * 渲染模板
     * @param file
     * @return
     */
    public String render(String file)
    {
        CodeNode code = readCode(file);
        code.render(_context);
        return _context.getStdout();
    }
    /**
     * 读取模板文件并解析生成渲染代码
     * @param file
     * @return
     */
    private CodeNode readCode(String file)
    {
        File f = new File(file);
        long lastModified = f.lastModified();
        TemplateCode temp = TempCache.getOrDefault(file, null);
        if(temp != null)
        {
            if(temp.lastModified == lastModified)
            {
                return temp.code;
            }
        }
        try
        {
            List<String> content = Files.readAllLines(Paths.get(file), StandardCharsets.UTF_8);
            CodeNode code = TemplateParser.parse(content);
            if(temp == null)
            {
                temp = new TemplateCode();
                temp.code = code;
                temp.lastModified = lastModified;
                TempCache.put(file, temp);
            }else
            {
                temp.code = code;
                temp.lastModified = lastModified;
            }
            return code;
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    /**
     * 用于保存模板文件解析生成的代码
     * 作为缓存使用，加快后续渲染流程
     */
    class TemplateCode
    {
        public long lastModified = 0;
        public CodeNode code = null;
    }

}
