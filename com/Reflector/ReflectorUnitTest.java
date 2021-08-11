package com.Reflector;

import com.NetlabUT.*;
import sun.reflect.ReflectionFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/** extended class from {@link UnitTest}, add support to {@link java.lang.reflect.Method}.
 * It helps translate a valid {@link Throwable} caused by underlying {@code Method}
 * instead from {@link java.lang.reflect.Method#invoke(Object, Object...)}
 * @author Ramadhan Kalih Sewu
 * @version 1.0
 */
public abstract class ReflectorUnitTest extends UnitTest
{
    protected ReflectorUnitTest() {}
    protected ReflectorUnitTest(String testName) { super(testName); }

    /** will try to find an object that has access to the method.
     * If your {@code obj} doesn't have access to the {@method}, calling this will make your {@code method}
     * accessible for the future even if it's private.
     * @param classR Class that contain the {@code method}
     * @param method Method that needs an access
     * @param obj Object that tries to invoke the {@code method}
     * @return an Object that has access or {@code obj} if fail
     * @implNote Throwable will occur if fail to create new object in order to get acess to instance method.
     * Throwable triggered by: {@link Constructor#newInstance(Object...)} and {@link Class#getDeclaredConstructor(Class[])}
     */
    protected Object getForceAccess(ClassR classR, Method method, Object obj)
            throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException
    {
        // set accessible for private modifier
        if (!method.canAccess(obj))
            method.setAccessible(true);
        // if static method, we need an object
        if (obj == null && !Modifier.isStatic(method.getModifiers()))
        {
            final Class<?> myClass = classR.getContainingClass();
            if (myClass == null) return obj;
            final ReflectionFactory reflection = ReflectionFactory.getReflectionFactory();
            final Constructor<?> constructor = reflection.newConstructorForSerialization(
                    myClass, Object.class.getDeclaredConstructor(new Class[0]));
            obj = constructor.newInstance(new Object[0]);
        }
        return obj;
    }

    /** calling a {@link java.lang.reflect.Method}. When invoked, expect to throw any instance of
     * {@link java.lang.Throwable}. It only counts from {@link java.lang.reflect.InvocationTargetException}
     * when executing {@link java.lang.reflect.Method#invoke(Object, Object...)} */
    protected void assumeThrows(Method method, Object obj, Object... args)
    {
        Metric<Object> metric = Benchmark.run(() -> method.invoke(obj, args));
        if (metric.isThrowing())
        {
            if (metric.throwable instanceof InvocationTargetException)
            {
                metric.throwable = ((InvocationTargetException) metric.throwable).getTargetException();
                boolean correct = Logical.throwing(metric.throwable);
                record(new Profile<Object>(metric, Throwable.class, Status.THROWS, correct, null));
                return;
            }
            metric.throwable = new ReflectorException(method, metric.throwable);
        }
        record(new Profile<Object>(metric, Throwable.class, Status.THROWS, false, null));
    }

    /** calling a {@link java.lang.reflect.Method}. When invoked, expect to throw a type of {@code expected}.
     * It only counts from {@link java.lang.reflect.InvocationTargetException}
     * when executing {@link java.lang.reflect.Method#invoke(Object, Object...)} */
    protected void assumeThrows(Class<?> expected, Method method, Object obj, Object... args)
    {
        Metric<Object> metric = Benchmark.run(() -> method.invoke(obj, args));
        if (metric.isThrowing())
        {
            if (metric.throwable instanceof InvocationTargetException)
            {
                metric.throwable = ((InvocationTargetException) metric.throwable).getTargetException();
                boolean correct = Logical.throwing(expected, metric.throwable);
                record(new Profile<Object>(metric, expected, Status.THROWS_TYPE, correct, null));
                return;
            }
            metric.throwable = new ReflectorException(method, metric.throwable);
        }
        record(new Profile<Object>(metric, expected, Status.THROWS_TYPE, false, null));
    }

    /** When invoked, expect to throws a type of {@code expected}. It only counts from
     * {@link java.lang.reflect.InvocationTargetException} when executing
     * {@link java.lang.reflect.Method#invoke(Object, Object...)} */
    protected void assumeThrows(ClassR expected, Method method, Object obj, Object... args)
    { assumeThrows(expected.getContainingClass(), method, obj, args); }

    /** When invoked using {@link java.lang.reflect.Method#invoke(Object, Object...)},
     * expect to return {@code null} */
    protected void assumeNull(Method method, Object obj, Object... args)
    { assumeNull(null, method, obj, args); }

    /** When invoked using {@link java.lang.reflect.Method#invoke(Object, Object...)},
     * expect to return {@code null} */
    protected void assumeNull(String message, Method method, Object obj, Object... args)
    { record(message, Status.REFERENCE, null, method, obj, args); }

    /** When invoked using {@link java.lang.reflect.Method#invoke(Object, Object...)},
     * expect to return <strong>not</strong> {@code null} */
    protected void assumeNotNull(Method method, Object obj, Object... args)
    { assumeNotNull(null, method, obj, args); }

    /** When invoked using {@link java.lang.reflect.Method#invoke(Object, Object...)},
     * expect to return <strong>not</strong> {@code null} */
    protected void assumeNotNull(String message, Method method, Object obj, Object... args)
    { record(message, Status.NOT_REFERENCE, null, method, obj, args); }

    /** When invoked using {@link java.lang.reflect.Method#invoke(Object, Object...)},
     * expect to return a reference to {@code expected} (referencing the same object) */
    protected void assumeSame(Object expected, Method method, Object obj, Object... args)
    { assumeSame(null, expected, method, obj, args); }

    /** When invoked using {@link java.lang.reflect.Method#invoke(Object, Object...)},
     * expect to return a reference to {@code expected} (referencing the same object) */
    protected void assumeSame(String message, Object expected, Method method, Object obj, Object... args)
    { record(message, Status.REFERENCE, expected, method, obj, args); }

    /** When invoked using {@link java.lang.reflect.Method#invoke(Object, Object...)},
     * expect to return an object which <strong>not</strong> referencing to {@code expected} */
    protected void assumeNotSame(Object expected, Method method, Object obj, Object... args)
    { assumeNotSame(null, expected, method, obj, args); }

    /** When invoked using {@link java.lang.reflect.Method#invoke(Object, Object...)},
     * expect to return an object which <strong>not</strong> referencing to {@code expected} */
    protected void assumeNotSame(String message, Object expected, Method method, Object obj, Object... args)
    { record(message, Status.NOT_REFERENCE, expected, method, obj, args); }

    /** When invoked using {@link java.lang.reflect.Method#invoke(Object, Object...)},
     * expect to return {@code true} */
    protected void assumeTrue(Method method, Object obj, Object... args)
    { assumeTrue(null, method, obj, args); }

    /** When invoked using {@link java.lang.reflect.Method#invoke(Object, Object...)},
     * expect to return {@code true} */
    protected void assumeTrue(String message, Method method, Object obj, Object... args)
    { record(message, Status.EQUAL, true, method, obj, args); }

    /** When invoked using {@link java.lang.reflect.Method#invoke(Object, Object...)},
     * expect to return {@code false} */
    protected void assumeFalse(Method method, Object obj, Object... args)
    { assumeFalse(null, method, obj, args); }

    /** When invoked using {@link java.lang.reflect.Method#invoke(Object, Object...)},
     * expect to return {@code false} */
    protected void assumeFalse(String message, Method method, Object obj, Object... args)
    { record(message, Status.EQUAL, false, method, obj, args); }

    /** When invoked using {@link java.lang.reflect.Method#invoke(Object, Object...)},
     * expect to return an object equal to {@code expected} which determined by {@link java.lang.Object#equals(Object)} */
    protected void assumeEquals(Object expected, Method method, Object obj, Object... args)
    { assumeEquals(null, expected, method, obj, args); }

    /** When invoked using {@link java.lang.reflect.Method#invoke(Object, Object...)},
     * expect to return an object equal to {@code expected} (determined by {@link java.lang.Object#equals(Object)}) */
    protected void assumeEquals(String message, Object expected, Method method, Object obj, Object... args)
    { record(message, Status.EQUAL, expected, method, obj, args); }

    /** When invoked using {@link java.lang.reflect.Method#invoke(Object, Object...)},
     * expect to return an object <strong>not</strong> equal to {@code expected} (determined by {@link java.lang.Object#equals(Object)}) */
    protected void assumeNotEquals(Object expected, Method method, Object obj, Object... args)
    { assumeNotEquals(null, expected, method, obj, args); }

    /** When invoked using {@link java.lang.reflect.Method#invoke(Object, Object...)},
     * expect to return an object <strong>not</strong> equal to {@code expected} (determined by {@link java.lang.Object#equals(Object)}) */
    protected void assumeNotEquals(String message, Object expected, Method method, Object obj, Object... args)
    { record(message, Status.NOT_EQUAL, expected, method, obj, args); }

    /** When invoked using {@link java.lang.reflect.Method#invoke(Object, Object...)},
     * expect to return an array of object. And when compared to {@code expected}, expect to have same length and
     * iteratively equal (determined by {@link java.lang.Object#equals(Object)}) */
    protected void assumeArrayEquals(Object[] expected, Method method, Object obj, Object... args)
    { assumeArrayEquals(null, expected, method, obj, args); }

    /** When invoked using {@link java.lang.reflect.Method#invoke(Object, Object...)},
     * expect to return an array of object. And when compared to {@code expected}, expect to have same length and
     * iteratively equal (determined by {@link java.lang.Object#equals(Object)}) */
    protected void assumeArrayEquals(String message, Object[] expected, Method method, Object obj, Object... args)
    { record(message, Status.ARRAY_EQUAL, expected, method, obj, args); }

    /** When invoked using {@link java.lang.reflect.Method#invoke(Object, Object...)},
     * expect to return an array of object. And when compared to {@code expected}, expect either have different length
     * or <strong>not</strong> iteratively equal (determined by {@link java.lang.Object#equals(Object)}) */
    protected void assumeArrayNotEquals(Object[] expected, Method method, Object obj, Object... args)
    { assumeArrayNotEquals(null, expected, method, obj, args); }

    /** When invoked using {@link java.lang.reflect.Method#invoke(Object, Object...)},
     * expect to return an array of object. And when compared to {@code expected}, expect either have different length
     * or <strong>not</strong> iteratively equal (determined by {@link java.lang.Object#equals(Object)}) */
    protected void assumeArrayNotEquals(String message, Object[] expected, Method method, Object obj, Object... args)
    { record(message, Status.ARRAY_NOT_EQUAL, expected, method, obj, args); }

    /** run benchmark for {@link java.lang.reflect.Method#invoke(Object, Object...)} and check the
     * assumption using comparator that takes 2 args from {@code reference} and return value from {@code method}
     * @param message information about the assumption
     * @param comparison type of comparison expected. It will determine the comparator
     * @param reference argument passed to comparator
     * @param method a method to invoke
     * @param obj an instance of a class to run a {@code method}, can {@code null} if static method
     * @param args an argument passed to {@code method}
     * @param <T> type of object that evaluated
     */
    private <T> void record(String message, int comparison, T reference, Method method, Object obj, Object... args)
    {
        boolean correct = false;
        Metric<Object> metric = Benchmark.run(() -> method.invoke(obj, args));
        if (metric.isThrowing())
        {
            metric.throwable = metric.throwable instanceof InvocationTargetException ?
                    ((InvocationTargetException) metric.throwable).getTargetException() :
                    new ReflectorException(method, metric.throwable);
        }
        else if (metric.returns != null && reference != null && metric.returns.getClass() != reference.getClass())
        {
            metric.throwable = new MismatchTypeException(reference.getClass(), metric.returns.getClass());
        }
        else
        {
            correct = compare(reference, metric.returns, comparison);
        }
        record(new Profile<>(metric, reference, comparison, correct, message));
    }

}
