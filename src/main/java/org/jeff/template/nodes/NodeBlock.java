package org.jeff.template.nodes;

import org.jeff.template.RenderContext;

import java.util.ArrayList;
import java.util.List;

public class NodeBlock extends Node
{
    private final String header;
    private final List<Node> nodes = new ArrayList<>();
    public NodeBlock(String h, int line)
    {
        super(line);
        this.header = h;
    }

    public void addChild(Node node)
    {
        nodes.add(node);
    }

    @Override
    public String render(RenderContext context)
    {
        for(Node node : nodes)
        {
            node.render(context);
        }
        return "";
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s#%d\r\n", header, line));
        for(Node node : nodes)
        {
            sb.append(node.toString());
            sb.append("\r\n");
        }
        return sb.toString();
    }
}
