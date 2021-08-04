package com.Reflector;

public class MismatchTypeException extends Throwable
{
    Class<?> a;
    Class<?> b;

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
