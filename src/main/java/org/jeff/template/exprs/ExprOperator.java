package org.jeff.template.exprs;


import java.util.List;
import java.util.Map;

public class ExprOperator
{
    // 相等判断
    public static boolean eq(Object a, Object b)
    {
        if(a == null && b == null) return true;
        if(a == null || b == null) return false;
        return a.equals(b);
    }

    public static boolean neq(Object a, Object b)
    {
        return !eq(a, b);
    }

    public static Object add(Object a, Object b)
    {
        if(a instanceof String || b instanceof String)
        {
            return new String(a.toString() + b.toString());
        }
        if(a instanceof Integer)
        {
            if(b instanceof Integer) return (Integer)a + (Integer)b;
            if(b instanceof Float) return (Integer)a + (Float)b;
            throw new RuntimeException("");
        }
        if(b instanceof Float)
        {
            return (Float)a + (Float)b;
        }
        throw  new RuntimeException("Cannot add:" + a.toString() + " + " + b.toString());
    }

    public static Object sub(Object a, Object b)
    {
        if(a instanceof Number && b instanceof Number) {
            if (a instanceof Integer) {
                if (b instanceof Integer) return (Integer) a - (Integer) b;
                if (b instanceof Float) return (Integer) a - (Float) b;
                throw new RuntimeException("");
            }
            if (b instanceof Float) {
                return (Float) a - (Float) b;
            }
        }
        throw  new RuntimeException("Cannot add:" + a.toString() + "-" + b.toString());
    }

    public static Object mul(Object a, Object b)
    {
        if(a instanceof Number && b instanceof Number) {
            if (a instanceof Integer && b instanceof Integer) {
                return (Integer) a * (Integer) b;
            }
            if (a instanceof Float || b instanceof Float) {
                return (Float)a * (Float)b;
            }
        }
        throw  new RuntimeException("Cannot add:" + a.toString() + " * " + b.toString());
    }

    public static Object div(Object a, Object b)
    {
        if(a instanceof Number && b instanceof Number) {
            if (a instanceof Integer && b instanceof Integer) {
                return (Integer) a / (Integer) b;
            }
            if (a instanceof Float || b instanceof Float) {
                return (Float)a / (Float)b;
            }
        }
        throw  new RuntimeException("Cannot add:" + a.toString() + " / " + b.toString());
    }

    public static Object mod(Object a, Object b)
    {
        if(a instanceof Number && b instanceof Number) {
            if (a instanceof Integer && b instanceof Integer) {
                return (Integer) a % (Integer) b;
            }
            if (a instanceof Float || b instanceof Float) {
                return (Float)a % (Float)b;
            }
        }
        throw  new RuntimeException("Cannot add:" + a.toString() + " % " + b.toString());
    }

    public static boolean lt(Object a, Object b)
    {
        if(a instanceof Number && b instanceof Number)
        {
            if (a instanceof Integer && b instanceof Integer) {
                return (Integer) a < (Integer) b;
            }
            if (a instanceof Float || b instanceof Float) {
                return (Float)a < (Float)b;
            }
        }
        throw  new RuntimeException("Cannot add:" + a.toString() + " < " + b.toString());
    }

    public static boolean lte(Object a, Object b)
    {
        if(a instanceof Number && b instanceof Number)
        {
            if (a instanceof Integer && b instanceof Integer) {
                return (Integer) a <= (Integer) b;
            }
            if (a instanceof Float || b instanceof Float) {
                return (Float)a <= (Float)b;
            }
        }
        throw  new RuntimeException("Cannot add:" + a.toString() + " <= " + b.toString());
    }

    public static boolean gt(Object a, Object b)
    {
        if(a instanceof Number && b instanceof Number)
        {
            if (a instanceof Integer && b instanceof Integer) {
                return (Integer) a > (Integer) b;
            }
            if (a instanceof Float || b instanceof Float) {
                return (Float)a > (Float)b;
            }
        }
        throw  new RuntimeException("Cannot add:" + a.toString() + " > " + b.toString());
    }

    public static boolean gte(Object a, Object b)
    {
        if(a instanceof Number && b instanceof Number)
        {
            if (a instanceof Integer && b instanceof Integer) {
                return (Integer) a >= (Integer) b;
            }
            if (a instanceof Float || b instanceof Float) {
                return (Float)a >= (Float)b;
            }
        }
        throw  new RuntimeException("Cannot add:" + a.toString() + " >= " + b.toString());
    }

    public static boolean and(Object a, Object b)
    {
        return toBool(a) && toBool(b);
    }

    public static boolean or(Object a, Object b)
    {
        return toBool(a) || toBool(b);
    }

    public static boolean not(Object a)
    {
        return !toBool(a);
    }

    public static Object neg(Object a)
    {
        if(a instanceof Number)
        {
            if(a instanceof Integer) return -(int)a;
            return -(float)a;
        }
        throw  new RuntimeException("Cannot negative:" + a.toString());
    }

    public static Object xor(Object a)
    {
        if(a instanceof Integer)
        {
            return ~(int)a;
            //  if(a.raw() instanceof Integer )
            // return new JsNumber(~(()((JsNumber) a).value));
        }
        throw  new RuntimeException("Cannot xor:" + a.toString());
    }

    public static Object incr(Object a)
    {
        if(!(a instanceof Number)) throw new RuntimeException("not number");
        if(a instanceof Integer) return (Integer)a + 1;
        return (float)a + 1.0;
    }

    public static Object decr(Object a)
    {
        if(!(a instanceof Number)) throw new RuntimeException("not number");
        if(a instanceof Integer) return (Integer)a - 1;
        return (float)a - 1.0;
    }

    public static Object getIndex(Object target, Object key)
    {
        if(target instanceof Indexable)
        {
            return ((Indexable)target).get(key);
        }
        else if(target instanceof List)
        {
            Integer index = (Integer) key;
            return ((List)target).get(index);
        }else if(target instanceof Map)
        {
            String index =(String) key;
            return ((Map)target).get(index);
        }
        throw new RuntimeException("Cannot index into " + target.toString());
    }

    public static void setIndex(Object target, Object key, Object value)
    {
        if(target instanceof Indexable)
        {
            ((Indexable)target).set(key, value);
            return;
        }else if(target instanceof List)
        {
            Integer index = (Integer) key;
            ((List)target).set(index, value);
            return;
        }else if(target instanceof Map)
        {
            ((Map)target).put(key, value);
            return;
        }
        throw new RuntimeException("Cannot index into " + target.toString());
    }

    public static boolean toBool(Object a)
    {
        if(a == null) return false;
        if(a instanceof Boolean) return (boolean) a;
        if(a instanceof Integer) return (int)a != 0;
        if(a instanceof Float) return (float)a != 0.0;
        if(a instanceof String) return !((String)a).isEmpty();
        if(a instanceof List) return ((List) a).size() > 0;
        if(a instanceof Map) return ((Map) a).size() > 0;
        return true;
    }
}
