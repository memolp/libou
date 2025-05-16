package org.jeff.jsw;

import org.jeff.jsw.objs.JsBuiltinFunction;
import org.jeff.jsw.objs.JsObject;
import org.jeff.jsw.objs.JsString;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplateEngine
{
    static JsEngine _engine;
    static
    {
        _engine = new JsEngine();
        _engine.setFunction("echo", new JsBuiltinFunction() {
            @Override
            public JsObject call(JsContext jsContext, JsObject... args)
            {
                if(args.length == 0) return new JsString("");
                if(args.length == 1) return args[0];
                String fmt = String.valueOf(args[0]);
                List<Object> _args = new LinkedList<>();
                _args.addAll(Arrays.asList(args).subList(1, args.length));
                _engine._sb.append(String.format(fmt, _args.toArray()));
                return null;
            }
        });
    }

    private static final Pattern codePattern = Pattern.compile("\\{%\\s*(.*?)\\s*%}", Pattern.DOTALL);

    public static String render(String template)
    {
        StringBuilder output = new StringBuilder();
        Matcher matcher = codePattern.matcher(template);
        int lastEnd = 0;
        while (matcher.find())
        {
            output.append(template, lastEnd, matcher.start()); // 静态 HTML
            String code = matcher.group(1);
            String jsOutput = runJS(code);
            output.append(jsOutput);
            lastEnd = matcher.end();
        }
        output.append(template.substring(lastEnd)); // 最后剩余部分
        return output.toString();
    }

    private static String runJS(String code)
    {
        _engine._sb = new StringBuilder();
        _engine.eval(code);
        return _engine._sb.toString();
    }

    public static String render(String filename, HashMap<String, Object> params)
    {
        HashMap<String, JsObject> contextVars = new HashMap<>();
        for(String key : params.keySet())
        {
            contextVars.put(key, JsObject.to(params.get(key)));
        }
        _engine.getContext().contextVars = contextVars;
        try
        {
            String template = new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8);
            return render(template);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return "";
    }
}
