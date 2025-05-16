package org.jeff.jsw.exprs;

import org.jeff.jsw.JsContext;
import org.jeff.jsw.objs.JsList;
import org.jeff.jsw.objs.JsObject;

import java.util.List;

public class ListExpr implements Expression
{
    List<Expression> items;
    public ListExpr(List<Expression> items)
    {
        this.items = items;
    }

    @Override
    public JsObject eval(JsContext context)
    {
        JsList list = new JsList();
        for(Expression e : items)
        {
            list.items.add(e.eval(context));
        }
        return list;
    }
}
