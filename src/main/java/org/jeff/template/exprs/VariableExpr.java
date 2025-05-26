package org.jeff.template.exprs;


import org.jeff.template.RenderContext;

public class VariableExpr implements Assignable
{
    private final String name;

    public VariableExpr(String name)
    {
        this.name = name;
    }

    @Override
    public Object eval(RenderContext context)
    {
        return context.get(name);
    }

    @Override
    public void set(RenderContext context, Object value)
    {
        context.set(name, value);
    }
}
