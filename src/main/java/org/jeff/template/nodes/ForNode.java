package org.jeff.template.nodes;

import org.jeff.template.RenderContext;
import org.jeff.template.exprs.Expression;

/**
 * 传统的for循环
 * {% for i=0; i < 10; i++ %}  => 分为初始化，条件表达式，更新表达式三部分用 分号";"隔开
 * 目前没有对齐进行扩展，需要在实际使用中看是否需要
 */
public class ForNode extends BlockNode
{
    /** 初始化，赋值表达式 */
    private final Expression initExpr;
    /** for循环的条件表达式 */
    private final Expression condExpr;
    /** 更新表达式，更新条件里面的某个 */
    private final Expression updateExpr;
    /** 传统for循环 */
    public ForNode(Expression init, Expression cond, Expression update, int line)
    {
        super(BlockNode.CODE_FOR, line);
        this.initExpr = init;
        this.condExpr = cond;
        this.updateExpr = update;
    }

    @Override
    public void render(RenderContext context)
    {
        context.pushVariables(); // 建立local作用域
        this.initExpr.eval(context);
        do {
            boolean r = (boolean)this.condExpr.eval(context);
            if(!r) break;
            try
            {
                super.render(context);
            }catch (ContinueException ignored) {  // 只需要子代码抛出即可，这边继续执行
            }catch (BreakException e)  // 中断循环
            {
                break;
            }
            this.updateExpr.eval(context);  // 更新表达式 => 针对++ -- 的前置和后置 后续业务再考虑要不要处理
        }while(true);
        context.popVariables();  // 弹出作用域
    }
}
