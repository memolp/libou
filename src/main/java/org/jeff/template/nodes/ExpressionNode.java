package org.jeff.template.nodes;

import org.jeff.template.RenderContext;
import org.jeff.template.exprs.Expression;

/**
 * 表达式节点
 * 包含在 {{ 表达式 }} 中间的部分
 */
public class ExpressionNode extends Node
{
    /** 表达式 */
    private final Expression expr;
    /** 用于标记当前表达式返回的内容是否需要进行转义 */
    private final boolean decode;  // 默认会转义

    public ExpressionNode(Expression text, int line)
    {
        super(line);
        this.expr = text;
        this.decode = true;
    }

    public ExpressionNode(Expression expr, boolean decode, int line)
    {
        super(line);
        this.expr = expr;
        this.decode = decode;
    }

    @Override
    public void render(RenderContext context)
    {
        Object value = expr.eval(context);
        if(value != null)
        {
            String raw_ = value.toString();
            if(this.decode)
            {
                context.echo(escape(raw_));
            }else
            {
                context.echo(raw_);
            }
            return;
        }
        throw new RuntimeException(String.format("null value! line:%d", line));
    }

    private static String escape(String input)
    {
        if (input == null) return null;
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
