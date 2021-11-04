package com.NetlabUT;

@Deprecated
public class MismatchTypeException extends Throwable
{
    Class<?> a;
    Class<?> b;

    public MismatchTypeException(Object a, Object b)
    {
        this.a = a == null ? null : a.getClass();
        this.b = b == null ? null : b.getClass();
    }
    public MismatchTypeException(Class<?> a, Class<?> b)
    {
        this.a = a;
        this.b = b;
    }

    @Override
    public String getMessage()
    {
        return String.format("Mismatch type of %s with %s", a.getName(), b.getName());
    }
}
