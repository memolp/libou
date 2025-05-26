package org.jeff.template.exprs;

import org.jeff.template.RenderContext;

import java.util.ArrayList;
import java.util.List;

public class ListExpr implements Expression
{
    private final List<Expression> items;
    public ListExpr(List<Expression> list)
    {
        this.items = list;
    }

    @Override
    public Object eval(RenderContext context)
    {
        List<Object> objects = new ArrayList<Object>();
        for(Expression e : items)
            objects.add(e.eval(context));
        return objects;
    }
}
