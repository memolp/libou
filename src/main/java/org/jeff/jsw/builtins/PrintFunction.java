package org.jeff.jsw.builtins;

import org.jeff.jsw.JsContext;
import org.jeff.jsw.objs.JsBuiltinFunction;
import org.jeff.jsw.objs.JsObject;

import java.util.LinkedList;
import java.util.List;

public class PrintFunction extends JsBuiltinFunction
{
    @Override
    public JsObject call(JsContext context, JsObject... args)
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
}
