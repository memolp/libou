package org.jeff.template;

import java.util.*;

/***
 * 渲染中的环境上下文，用于处理作用域和存储结果
 */
public class RenderContext
{
    /** 作用域栈 */
    private final Stack<Variables> variablesStack = new Stack<>();

    private StringBuilder stdout = new StringBuilder();

    public RenderContext(Map<String, Object> initial)
    {
        Variables global = new Variables(initial);
        this.variablesStack.push(global);
    }

    public Object get(String name)
    {
        return this.variablesStack.peek().get(name);
    }

    public void set(String name, Object value)
    {
        this.variablesStack.peek().set(name, value);
    }

    public void echo(String s)
    {
        stdout.append(s);
    }

    public String getStdout()
    {
        return stdout.toString();
    }
    /** 创建一个新的作用域 */
    public void pushVariables()
    {
        Variables vars = new Variables(this.variablesStack.peek());
        this.variablesStack.push(vars);
    }
    /** 弹出栈上面的最上面的作用域 */
    public void popVariables()
    {
        variablesStack.pop();
        if(variablesStack.isEmpty())
        {
            throw new RuntimeException("variablesStack is empty");
        }
    }

    /**
     * 变量的作用域
     */
    class Variables
    {
        private final Map<String, Object> variables = new HashMap<>();
        private final Variables parent;

        public Variables()
        {
            this.parent = null;
        }
        public Variables(Variables parent)
        {
            this.parent = parent;
        }

        public Variables(Map<String, Object> v)
        {
            this.variables.putAll(v);
            this.parent = null;
        }
        /** 设置变量的值 */
        public void set(String name, Object value)
        {
            this.variables.put(name, value);
        }
        /** 获取name变量的值 */
        public Object get(String name)
        {
            if(this.variables.containsKey(name)) return this.variables.get(name);
            if(this.parent != null) return parent.get(name);
            return null;
        }
        /** 判断变量是否存在 */
        public boolean contain(String name)
        {
            if(this.variables.containsKey(name)) return true;
            if(this.parent != null) return parent.contain(name);
            return false;
        }
    }
}
