package org.jeff.template.nodes;

import org.jeff.template.RenderContext;
import org.jeff.template.exprs.Expression;

/**
 * 变量节点 {{ xxx }} 这种就是变量节点，其中xxx可以是基础的数字、字符串、Java或者模板中设置的变量、表达式、函数调用等
 */
public class VariableNode extends Node
{
    /** 变量表达式 */
    private final Expression nodeExpression;
    /** 变量节点 */
    public VariableNode(Expression expr, int line)
    {
        super(line);
        this.nodeExpression = expr;
    }

    @Override
    public void render(RenderContext context)
    {
        // 计算变量的值，然后输出
        Object value = nodeExpression.eval(context);
        context.echo(value != null ? value.toString() : "");
    }
}
