package org.jeff.template.nodes;

import org.jeff.template.RenderContext;

import java.util.ArrayList;
import java.util.List;

/**
 * 包含代码身体段的节点 整个文件、for if等
 */
public abstract class BlockNode extends Node
{
    public static int CODE_BLOCK = 0;
    public static int CODE_IF = 1;
    public static int CODE_ELIF = 2;
    public static int CODE_ELSE =3;
    public static int CODE_FOR = 5;
    public static int CODE_FOREACH = 6;

    private final List<Node> nodes = new ArrayList<>();
    public final int blockType;

    public BlockNode(int type, int line)
    {
        super(line);
        this.blockType = type;
    }

    public void addChild(Node node)
    {
        nodes.add(node);
    }

    @Override
    public void render(RenderContext context)
    {
        for(Node node : nodes)
            node.render(context);
    }
}
