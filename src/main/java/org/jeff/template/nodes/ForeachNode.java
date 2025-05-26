package org.jeff.template.nodes;

import org.jeff.template.RenderContext;
import org.jeff.template.exprs.Expression;

/**
 * 迭代器遍历 => 支持变量List和Map
 * {% for k in iterable %}
 */
public class ForeachNode extends BlockNode
{
    /** 变量声明 */
    private final Expression varExpr;
    /** 迭代对象 */
    private final Expression iterableExpr;

    public ForeachNode(Expression variable, Expression iterable, int line)
    {
        super(BlockNode.CODE_FOREACH, line);
        this.varExpr = variable;
        this.iterableExpr = iterable;
    }

    @Override
    public void render(RenderContext context)
    {
        // 获取到变量名
        Object name = varExpr.eval(context);
        // 获取迭代对象
        Object iterable = iterableExpr.eval(context);
        if(!(iterable instanceof Iterable))
            throw new RuntimeException(String.format("%s is not Iterable! line:%d", iterableExpr, line));

        context.pushVariables(); // 创建local变量作用域
        for(Object item : (Iterable<?>)iterable)  // 开始执行迭代
        {
            context.set(name.toString(), item); //将当前的值跟临时变量关联
            try
            {
                super.render(context);  // 执行body部分
            }catch (ContinueException ignored){}
            catch (BreakException e)
            {
                break;
            }
        }
        context.popVariables();
    }
}
