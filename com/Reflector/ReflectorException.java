package com.Reflector;

import java.lang.reflect.Method;

public class ReflectorException extends Throwable
{
    private Method method;
    private Throwable throwable;

    public ReflectorException(Method method, Throwable throwable)
    {
        this.method = method;
        this.throwable = throwable;
    }

    @Override
    public String getMessage()
    {
        if (method == null)
            return "Attempting to invoke a null method";
        if (method != null && throwable instanceof NullPointerException)
            return "Attempting to invoke instance method with null object";
        return throwable.toString();
    }
}
