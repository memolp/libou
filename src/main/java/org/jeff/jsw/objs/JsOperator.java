package org.jeff.jsw.objs;

public class JsOperator
{
    // 相等判断
    public static JsBool eq(JsObject a, JsObject b)
    {
        if(a instanceof JsNull && b instanceof JsNull) return new JsBool(true);
        if(a instanceof JsNull || b instanceof JsNull) return new JsBool(false);
        if(a.raw().equals(b.raw())) return new JsBool(true);
        return new JsBool(false);
    }

    public static JsBool neq(JsObject a, JsObject b)
    {
        return new JsBool(!eq(a, b).value);
    }

    public static JsObject add(JsObject a, JsObject b)
    {
        if(a instanceof JsString || b instanceof JsString)
        {
            return new JsString(a.toString() + b.toString());
        }
        if(a instanceof JsNumber && b instanceof JsNumber)
        {
            return new JsNumber(((JsNumber) a).value + ((JsNumber) b).value);
        }
        throw  new RuntimeException("Cannot add:" + a.type() + " + " + b.type());
    }

    public static JsObject sub(JsObject a, JsObject b)
    {
        if(a instanceof JsNumber && b instanceof JsNumber)
        {
            return new JsNumber(((JsNumber) a).value - ((JsNumber) b).value);
        }
        throw  new RuntimeException("Cannot add:" + a.type() + " - " + b.type());
    }

    public static JsObject mul(JsObject a, JsObject b)
    {
        if(a instanceof JsNumber && b instanceof JsNumber)
        {
            return new JsNumber(((JsNumber) a).value * ((JsNumber) b).value);
        }
        throw  new RuntimeException("Cannot add:" + a.type() + " * " + b.type());
    }

    public static JsObject div(JsObject a, JsObject b)
    {
        if(a instanceof JsNumber && b instanceof JsNumber)
        {
            return new JsNumber(((JsNumber) a).value / ((JsNumber) b).value);
        }
        throw  new RuntimeException("Cannot add:" + a.type() + " / " + b.type());
    }

    public static JsObject mod(JsObject a, JsObject b)
    {
        if(a instanceof JsNumber && b instanceof JsNumber)
        {
            return new JsNumber(((JsNumber) a).value % ((JsNumber) b).value);
        }
        throw  new RuntimeException("Cannot add:" + a.type() + " % " + b.type());
    }

    public static JsBool lt(JsObject a, JsObject b)
    {
        if(a instanceof JsNumber && b instanceof JsNumber)
        {
            return new JsBool(((JsNumber) a).value < ((JsNumber) b).value);
        }
        throw  new RuntimeException("Cannot add:" + a.type() + " < " + b.type());
    }

    public static JsBool lte(JsObject a, JsObject b)
    {
        if(a instanceof JsNumber && b instanceof JsNumber)
        {
            return new JsBool(((JsNumber) a).value <= ((JsNumber) b).value);
        }
        throw  new RuntimeException("Cannot add:" + a.type() + " < " + b.type());
    }

    public static JsBool gt(JsObject a, JsObject b)
    {
        if(a instanceof JsNumber && b instanceof JsNumber)
        {
            return new JsBool(((JsNumber) a).value > ((JsNumber) b).value);
        }
        throw  new RuntimeException("Cannot add:" + a.type() + " > " + b.type());
    }

    public static JsBool gte(JsObject a, JsObject b)
    {
        if(a instanceof JsNumber && b instanceof JsNumber)
        {
            return new JsBool(((JsNumber) a).value >= ((JsNumber) b).value);
        }
        throw  new RuntimeException("Cannot add:" + a.type() + " > " + b.type());
    }

    public static JsBool and(JsObject a, JsObject b)
    {
        return new JsBool(toBool(a) && toBool(b));
    }

    public static JsBool or(JsObject a, JsObject b)
    {
        return new JsBool(toBool(a) || toBool(b));
    }

    public static JsBool not(JsObject a)
    {
        return new JsBool(!toBool(a));
    }

    public static JsObject neg(JsObject a)
    {
        if(a instanceof JsNumber) return new JsNumber(-((JsNumber) a).value);
        throw  new RuntimeException("Cannot negative:" + a.type());
    }

    public static JsObject xor(JsObject a)
    {
        if(a instanceof JsNumber)
        {
          //  if(a.raw() instanceof Integer )
           // return new JsNumber(~(()((JsNumber) a).value));
        }
        throw  new RuntimeException("Cannot xor:" + a.type());
    }

    public static JsObject incr(JsObject a)
    {
        if(!(a instanceof JsNumber)) throw new RuntimeException("not number");
        return new JsNumber(++((JsNumber) a).value);
    }

    public static JsObject decr(JsObject a)
    {
        if(!(a instanceof JsNumber)) throw new RuntimeException("not number");
        return new JsNumber(--((JsNumber) a).value);
    }

    public static JsObject getIndex(JsObject target, JsObject key)
    {
        if(target instanceof JsIndexable)
        {
            return ((JsIndexable)target).get(key);
        }
        throw new RuntimeException("Cannot index into " + target.type());
    }

    public static void setIndex(JsObject target, JsObject key, JsObject value)
    {
        if(target instanceof JsIndexable)
        {
            ((JsIndexable)target).set(key, value);
            return;
        }
        throw new RuntimeException("Cannot index into " + target.type());
    }

    public static boolean toBool(JsObject a)
    {
        if(a instanceof JsBool) return ((JsBool) a).value;
        if(a instanceof JsNull) return false;
        if(a instanceof JsNumber) return ((JsNumber) a).value != 0;
        if(a instanceof JsString) return !a.toString().isEmpty();
        if(a instanceof JsList) return !((JsList) a).items.isEmpty();
        if(a instanceof JsMap) return !((JsMap) a).items.isEmpty();
        return true;
    }
}
