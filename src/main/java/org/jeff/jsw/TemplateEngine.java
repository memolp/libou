package org.jeff.jsw;

import org.jeff.jsw.objs.JsBuiltinFunction;
import org.jeff.jsw.objs.JsObject;
import org.jeff.jsw.objs.JsString;
import org.jeff.jsw.statements.Statement;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplateEngine extends JsEngine
{
    private StringBuilder code_sb = new StringBuilder();
    @Override
    protected void loadBuiltin()
    {
        super.loadBuiltin();
        this.setFunction("echo", new JsBuiltinFunction() {
            @Override
            public JsObject call(JsContext jsContext, JsObject... args)
            {
                if(args.length == 0) return null;
                if(args.length == 1)
                {
                    code_sb.append(args[0].toString());
                    return null;
                }
                String fmt = String.valueOf(args[0]);
                List<Object> _args = new LinkedList<>();
                _args.addAll(Arrays.asList(args).subList(1, args.length));
                code_sb.append(String.format(fmt, _args.toArray()));
                return null;
            }
        });
    }

    private static final Pattern codePattern = Pattern.compile("\\{%\\s*(.*?)\\s*%}", Pattern.DOTALL);

    public String render(String template)
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

    private String runJS(String code)
    {
        code_sb = new StringBuilder();
        this.eval(code);
        return code_sb.toString();
    }
    static HashMap<String, Statement> temp_Cache = new HashMap<>();
    public String render(String filename, HashMap<String, Object> params)
    {
        HashMap<String, JsObject> contextVars = new HashMap<>();
        for(String key : params.keySet())
        {
            contextVars.put(key, JsObject.to(params.get(key)));
        }
        this.getContext().contextVars = contextVars;
        if(temp_Cache.containsKey(filename))  // TODO 增加模板缓存，减少语法树的建立消耗
        {
        }
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
