package com.Reflector;

import com.NetlabUT.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class ReflectorUnitTest extends UnitTest
{
    protected ReflectorUnitTest() {}
    protected ReflectorUnitTest(String testName) { super(testName); }

    protected void assumeThrows(Method method, Object obj, Object... args)
    {
        Metric<Object> metric = Benchmark.run((Executable) () -> method.invoke(obj, args));
        if (metric.isThrowing())
        {
            if (metric.throwable instanceof InvocationTargetException)
            {
                metric.throwable = ((InvocationTargetException) metric.throwable).getTargetException();
                boolean correct = Logical.throwing(metric.throwable);
                record(new Profile(metric, Throwable.class, Status.THROWS, correct, null));
                return;
            }
            metric.throwable = new ReflectorException(method, metric.throwable);
        }
        record(new Profile(metric, Throwable.class, Status.THROWS, false, null));
    }
    protected void assumeThrows(Class<?> expected, Method method, Object obj, Object... args)
    {
        Metric<Object> metric = Benchmark.run((Executable) () -> method.invoke(obj, args));
        if (metric.isThrowing())
        {
            if (metric.throwable instanceof InvocationTargetException)
            {
                metric.throwable = ((InvocationTargetException) metric.throwable).getTargetException();
                boolean correct = Logical.throwing(expected, metric.throwable);
                record(new Profile(metric, expected, Status.THROWS_TYPE, correct, null));
                return;
            }
            metric.throwable = new ReflectorException(method, metric.throwable);
        }
        record(new Profile(metric, expected, Status.THROWS_TYPE, false, null));
    }
    protected void assumeThrows(ClassR expected, Method method, Object obj, Object... args)
    {
        assumeThrows(expected.getContainingClass(), method, obj, args);
    }

    protected void assumeNull(Method method, Object obj, Object... args)                                      { assumeNull(null, method, obj, args); }
    protected void assumeNotNull(Method method, Object obj, Object... args)                                   { assumeNotNull(null, method, obj, args); }
    protected void assumeSame(Object expected, Method method, Object obj, Object... args)                     { assumeSame(null, expected, method, obj, args); }
    protected void assumeNotSame(Object expected, Method method, Object obj, Object... args)                  { assumeNotSame(null, expected, method, obj, args); }
    protected void assumeTrue(Method method, Object obj, Object... args)                                      { assumeTrue(null, method, obj, args); }
    protected void assumeFalse(Method method, Object obj, Object... args)                                     { assumeFalse(null, method, obj, args); }
    protected void assumeEquals(Object expected, Method method, Object obj, Object... args)                   { assumeEquals(null, expected, method, obj, args); }
    protected void assumeNotEquals(Object expected, Method method, Object obj, Object... args)                { assumeNotEquals(null, expected, method, obj, args); }
    protected void assumeArrayEquals(Object[] expected, Method method, Object obj, Object... args)            { assumeArrayEquals(null, expected, method, obj, args); }
    protected void assumeArrayNotEquals(Object[] expected, Method method, Object obj, Object... args)         { assumeArrayNotEquals(null, expected, method, obj, args); }

    protected void assumeNull(String message, Method method, Object obj, Object... args)
    { record(message, Status.REFERENCE, null, method, obj, args); }
    protected void assumeNotNull(String message, Method method, Object obj, Object... args)
    { record(message, Status.NOT_REFERENCE, null, method, obj, args); }
    protected void assumeSame(String message, Object expected, Method method, Object obj, Object... args)
    { record(message, Status.REFERENCE, expected, method, obj, args); }
    protected void assumeNotSame(String message, Object expected, Method method, Object obj, Object... args)
    { record(message, Status.NOT_REFERENCE, expected, method, obj, args); }
    protected void assumeTrue(String message, Method method, Object obj, Object... args)
    { record(message, Status.EQUAL, true, method, obj, args); }
    protected void assumeFalse(String message, Method method, Object obj, Object... args)
    { record(message, Status.EQUAL, false, method, obj, args); }
    protected void assumeEquals(String message, Object expected, Method method, Object obj, Object... args)
    { record(message, Status.EQUAL, expected, method, obj, args); }
    protected void assumeNotEquals(String message, Object expected, Method method, Object obj, Object... args)
    { record(message, Status.NOT_EQUAL, expected, method, obj, args); }
    protected void assumeArrayEquals(String message, Object[] expected, Method method, Object obj, Object... args)
    { record(message, Status.ARRAY_EQUAL, expected, method, obj, args); }
    protected void assumeArrayNotEquals(String message, Object[] expected, Method method, Object obj, Object... args)
    { record(message, Status.ARRAY_NOT_EQUAL, expected, method, obj, args); }

    private <T> void record(String message, Status comparison, T reference, Method method, Object obj, Object... args)
    {
        Metric<Object> metric = Benchmark.run((Executable) () -> method.invoke(obj, args));
        if (metric.isThrowing())
        {
            metric.throwable = metric.throwable instanceof InvocationTargetException ?
                    ((InvocationTargetException) metric.throwable).getTargetException() :
                    new ReflectorException(method, metric.throwable);
            record(new Profile(metric, reference, comparison, false, message));
            return;
        }
        boolean correct = compare(reference, metric.returns, comparison);
        record(new Profile(metric, reference, comparison, correct, message));
    }

}
