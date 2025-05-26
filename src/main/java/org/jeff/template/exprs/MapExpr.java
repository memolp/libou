package org.jeff.template.exprs;

import org.jeff.template.RenderContext;

import java.util.HashMap;
import java.util.Map;

public class MapExpr implements Expression
{
    private final Map<Expression, Expression> map_expr;
    public MapExpr(Map<Expression, Expression> m)
    {
        this.map_expr = m;
    }

    @Override
    public Object eval(RenderContext context)
    {
        Map<String, Object> map = new HashMap<>();
        for(Expression key: map_expr.keySet())
        {
            Object k = key.eval(context);
            Object v = map_expr.get(key).eval(context);
            map.put(k.toString(), v);
        }
        return map;
    }
}
