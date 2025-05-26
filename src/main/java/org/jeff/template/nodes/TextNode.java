package org.jeff.template.nodes;

import org.jeff.template.RenderContext;

/**
 * 文本节点 不在{{ }}  {% %}  {#  #} 之间的内容都是文本节点
 * 文本节点直接输出原有内容
 */
public class TextNode extends Node
{
    /** 原始文本 */
    private final String htmlContent;
    /** 文本节点 */
    public TextNode(String text, int line)
    {
        super(line);
        this.htmlContent = text;
    }
    /** 直接输出原始内容 */
    public void render(RenderContext context)
    {
        context.echo(htmlContent);
    }
}
