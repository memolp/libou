package org.jeff.template.nodes;

import org.jeff.template.RenderContext;

public class NodeText extends Node
{
    private final String text;

    public NodeText(String text, int line)
    {
        super(line);
        this.text = text;
    }

    public String render(RenderContext context)
    {
        context.echo(text);
        return text;
    }
}
