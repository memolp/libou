package org.jeff.template;

import org.jeff.template.nodes.Node;

import java.util.*;

public class RenderContext
{
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

        public void set(String name, Object value)
        {
            this.variables.put(name, value);
        }

        public Object get(String name)
        {
            if(this.variables.containsKey(name)) return this.variables.get(name);
            if(this.parent != null) return parent.get(name);
            return null;
        }

        public boolean contain(String name)
        {
            if(this.variables.containsKey(name)) return true;
            if(this.parent != null) return parent.contain(name);
            return false;
        }
    }

    private final Stack<Variables> variablesStack = new Stack<>();

    private final Stack<Boolean> ifStack = new Stack<>();
    private boolean breakFlag = false;
    private boolean continueFlag = false;
    private List<Node> body = new ArrayList<>();
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

    public void pushVariables()
    {
        Variables vars = new Variables(this.variablesStack.peek());
        this.variablesStack.push(vars);
    }

    public void popVariables()
    {
        variablesStack.pop();
        if(variablesStack.isEmpty())
        {
            throw new RuntimeException("variablesStack is empty");
        }
    }

    public void pushIf(boolean condition) {ifStack.push(condition);}
    public void popIf() {ifStack.pop();}
    public boolean isCurrentIfTrue(){ return ifStack.isEmpty() || ifStack.peek();}
    public void setBreakFlag(boolean flag) {breakFlag = flag;}
    public boolean isBreakFlag() {return breakFlag;}
    public void clearBreak(){this.breakFlag = false;}
    public void setContinueFlag(boolean flag) {continueFlag = flag;}
    public boolean isContinueFlag() {return continueFlag;}
    public void clearContinue(){continueFlag = false;}
    public void setBody(List<Node> body){this.body = body;}
    public List<Node> getBody() {return body;}
}
