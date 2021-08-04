package com.Reflector;

import java.lang.reflect.Method;

/** Throwable used by {@link ReflectorUnitTest} defines a problem causeed by
 * {@link java.lang.reflect.Method#invoke(Object, Object...)} which not sources from underlying method.
 * @author Ramadhan Kalih Sewu
 * @version 1.0
 */
public class ReflectorException extends Throwable
{
    private final Method method;
    private final Throwable throwable;

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
        else if (throwable instanceof NullPointerException)
            return "Attempting to invoke instance method with null object";
        return throwable.toString();
    }
}
