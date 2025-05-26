package org.jeff.template.nodes;

import org.jeff.template.RenderContext;

public abstract class Node
{
    public final int line;
    public Node(int line)
    {
        this.line = line;
    }

    public abstract String render(RenderContext context);
}
