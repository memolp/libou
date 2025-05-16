package org.jeff.jsw.exprs;

import org.jeff.jsw.JsContext;
import org.jeff.jsw.objs.JsMap;
import org.jeff.jsw.objs.JsObject;

import java.util.Map;

public class MapExpr implements Expression
{
    Map<Expression, Expression> items;
    public MapExpr(Map<Expression, Expression> items)
    {
        this.items = items;
    }

    @Override
    public JsObject eval(JsContext context)
    {
        JsMap map = new JsMap();
        for(Expression key : items.keySet())
        {
            JsObject k = key.eval(context);
            JsObject v = items.get(key).eval(context);
            map.items.put(k, v);
        }
        return map;
    }
}
