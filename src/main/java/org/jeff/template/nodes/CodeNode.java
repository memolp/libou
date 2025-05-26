package org.jeff.template.nodes;

/**
 * 代码节点，包含全部的其他节点
 */
public class CodeNode extends BlockNode
{
    public CodeNode(int line)
    {
        super(BlockNode.CODE_BLOCK, line);
    }
}
