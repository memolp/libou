package org.jeff.template.nodes;

import org.jeff.template.RenderContext;

/**
 * 模板语言中各个部分节点
 * HTML模板
 * {{ 表达式(ExpressionNode) }}
 * {% if 条件表达式 (BranchNode - ExpressionNode) %}
 * {% elif 条件表达式 (BranchNode - ExpressionNode) %}
 * {% else (BranchNode) %}
 * {% for 赋值表达式；条件表达式；更新表达式%}  - 传统for循环
 * {% for 变量 in  迭代器 %} -- 迭代循环
 * {% break  %}
 * {% content %}
 * {% set 赋值表达式 %}
 * {% raw 表达式 %}
 * {% end %}  --
 */
public abstract class Node
{
    /** 用于记录当前节点所在的行，方便错误定位 */
    public final int line;
    /** 基类节点 */
    public Node(int line)
    {
        this.line = line;
    }
    /** 渲染该节点 */
    public abstract void render(RenderContext context);
}
