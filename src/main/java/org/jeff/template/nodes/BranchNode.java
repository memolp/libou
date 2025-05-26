package org.jeff.template.nodes;

import org.jeff.template.RenderContext;
import org.jeff.template.exprs.Expression;

/**
 * 分支节点
 * {% if 条件 %}  {% elif 条件  %} {% else %}
 */
public class BranchNode extends BlockNode
{
    public static int BRANCH_IF = 0;
    public static int BRANCH_ELIF = 1;
    public static int BRANCH_ELSE = 2;

    /** 条件表达式 */
    private final Expression condExpr;
    /** 用于标记当前分支的类型 */
    public final int branchType;

    public BranchNode(int type, Expression cond, int line)
    {
        super(type + 1, line);
        this.branchType = type;
        this.condExpr = cond;
    }

    @Override
    public void render(RenderContext context)
    {
        if(this.condExpr != null)
        {
            boolean v = (boolean)this.condExpr.eval(context);
            if(!v) return;
        }
        context.pushVariables();  // 创建局部作用域
        super.render(context);
        context.popVariables();
    }
}
