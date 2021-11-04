package com.Reflector;

import com.NetlabUT.*;
import com.NetlabUT.Executable;
import sun.reflect.ReflectionFactory;

import java.lang.reflect.*;
import java.util.function.Function;
import java.util.function.Supplier;

/** extended class from {@link UnitTest}, add support to {@link java.lang.reflect.Method}.
 * It helps translate a valid {@link Throwable} caused by underlying {@code Method}
 * instead from {@link java.lang.reflect.Method#invoke(Object, Object...)}
 * @author Ramadhan Kalih Sewu
 * @version 1.2
 */
public abstract class ReflectorUnitTest extends UnitTest
{
    /** call {@link UnitTest#UnitTest()} */
    protected ReflectorUnitTest() {}
    /** call {@link UnitTest#UnitTest(String)} */
    protected ReflectorUnitTest(String testName) { super(testName); }

    protected void assumeModifier(ReflectorModifier modifier, Class<?> clazz)
    { assumeModifier(null, modifier, clazz); }
    protected void assumeModifier(ReflectorModifier modifier, Method method)
    { assumeModifier(null, modifier, method); }
    protected void assumeModifier(ReflectorModifier modifier, Constructor<?> constructor)
    { assumeModifier(null, modifier, constructor); }
    protected void assumeModifier(ReflectorModifier modifier, Field field)
    { assumeModifier(null, modifier, field); }

    protected void assumeModifier(String message, ReflectorModifier modifier, Class<?> clazz)
    { recordModifierAssumption(message, modifier, clazz == null, Logic.isModifier(modifier, clazz)); }
    protected void assumeModifier(String message, ReflectorModifier modifier, Method method)
    { recordModifierAssumption(message, modifier, method == null, Logic.isModifier(modifier, method)); }
    protected void assumeModifier(String message, ReflectorModifier modifier, Constructor<?> constructor)
    { recordModifierAssumption(message, modifier, constructor == null, Logic.isModifier(modifier, constructor)); }
    protected void assumeModifier(String message, ReflectorModifier modifier, Field field)
    { recordModifierAssumption(message, modifier, field == null, Logic.isModifier(modifier, field)); }

    private void recordModifierAssumption(String message, ReflectorModifier modifier, boolean isNull, boolean correct)
    {
        Metric<Object> metric = new Metric<>(correct ? modifier : "other", 0, null);
        if (isNull) metric.returns = "null";
        Profile<Object> profile = new Profile<>(metric, modifier, Status.EQUAL, correct, message);
        record(profile);
    }

    /** It will run {@link java.lang.reflect.Method#invoke(Object, Object...)} through
     * {@link Benchmark#run(Executable)}. When invoked, expect to throw a type of {@link java.lang.Throwable}.
     * This was done by observing an {@link java.lang.reflect.InvocationTargetException}
     * @param method a method that will be invoked
     * @param obj an object to run the {@code method} (can be null if static method)
     * @param args an argument passed to the {@code method} */
    protected void assumeThrows(String message, Method method, Object obj, Object... args)
    {
        Metric<Object> metric = Benchmark.run(() -> method.invoke(obj, args));
        if (metric.isThrowing())
        {
            if (metric.throwable instanceof InvocationTargetException)
            {
                metric.throwable = ((InvocationTargetException) metric.throwable).getTargetException();
                boolean correct = Logical.throwing(metric.throwable);
                record(new Profile<Object>(metric, Throwable.class, Status.THROWS, correct, message));
                return;
            }
            metric.throwable = new ReflectorException(method, metric.throwable);
        }
        record(new Profile<Object>(metric, Throwable.class, Status.THROWS, false, null));
    }

    /** It will run {@link java.lang.reflect.Method#invoke(Object, Object...)} through
     * {@link Benchmark#run(Executable)}. When invoked, expect to throw a type of {@code expected}.
     * This was done by observing an {@link java.lang.reflect.InvocationTargetException}
     * @param expected a type that is expected from throws event caused by {@code method}
     * @param method a method that will be invoked
     * @param obj an object to run the {@code method} (can be null if static method)
     * @param args an argument passed to the {@code method} */
    protected void assumeThrows(String message, Class<?> expected, Method method, Object obj, Object... args)
    {
        Metric<Object> metric = Benchmark.run(() -> method.invoke(obj, args));
        if (metric.isThrowing())
        {
            if (metric.throwable instanceof InvocationTargetException)
            {
                metric.throwable = ((InvocationTargetException) metric.throwable).getTargetException();
                boolean correct = Logical.throwing(expected, metric.throwable);
                record(new Profile<Object>(metric, expected, Status.THROWS_TYPE, correct, message));
                return;
            }
            metric.throwable = new ReflectorException(method, metric.throwable);
        }
        record(new Profile<Object>(metric, expected, Status.THROWS_TYPE, false, null));
    }

    /** It will run {@link java.lang.reflect.Method#invoke(Object, Object...)} through
     * {@link Benchmark#run(Executable)}. When invoked, expect to throw a type of {@link java.lang.Throwable}.
     * This was done by observing an {@link java.lang.reflect.InvocationTargetException}
     * @param method a method that will be invoked
     * @param obj an object to run the {@code method} (can be null if static method)
     * @param args an argument passed to the {@code method} */
    protected void assumeThrows(Method method, Object obj, Object... args)
    { assumeThrows((String) null, method, obj, args); }

    /** It will run {@link java.lang.reflect.Method#invoke(Object, Object...)} through
     * {@link Benchmark#run(Executable)}. When invoked, expect to throw a type of {@code expected}.
     * This was done by observing an {@link java.lang.reflect.InvocationTargetException}
     * @param expected a type that is expected from throws event caused by {@code method}
     * @param method a method that will be invoked
     * @param obj an object to run the {@code method} (can be null if static method)
     * @param args an argument passed to the {@code method} */
    protected void assumeThrows(Class<?> expected, Method method, Object obj, Object... args)
    { assumeThrows(null, expected, method, obj, args); }

    /** When invoked using {@link java.lang.reflect.Method#invoke(Object, Object...)},
     * expect to return {@code null}
     * @param method a method to invoke
     * @param obj an object to run the {@code method} (can be null if static method)
     * @param args an argument passed to the {@code method} */
    protected void assumeNull(Method method, Object obj, Object... args)
    { assumeNull(null, method, obj, args); }

    /** When invoked using {@link java.lang.reflect.Method#invoke(Object, Object...)},
     * expect to return {@code null}
     * @param message a string that gives information about this test / method calls
     * @param method a method to invoke
     * @param obj an object to run the {@code method} (can be null if static method)
     * @param args an argument passed to the {@code method} */
    protected void assumeNull(String message, Method method, Object obj, Object... args)
    { record(message, Status.REFERENCE, null, method, obj, args); }

    /** When invoked using {@link java.lang.reflect.Method#invoke(Object, Object...)},
     * expect to return <strong>not</strong> {@code null}
     * @param method a method to invoke
     * @param obj an object to run the {@code method} (can be null if static method)
     * @param args an argument passed to the {@code method} */
    protected void assumeNotNull(Method method, Object obj, Object... args)
    { assumeNotNull(null, method, obj, args); }

    /** When invoked using {@link java.lang.reflect.Method#invoke(Object, Object...)},
     * expect to return <strong>not</strong> {@code null}
     * @param message a string that gives information about this test / method calls
     * @param method a method to invoke
     * @param obj an object to run the {@code method} (can be null if static method)
     * @param args an argument passed to the {@code method} */
    protected void assumeNotNull(String message, Method method, Object obj, Object... args)
    { record(message, Status.NOT_REFERENCE, null, method, obj, args); }

    /** When invoked using {@link java.lang.reflect.Method#invoke(Object, Object...)},
     * expect to return a reference to {@code expected} (referencing the same object)
     * @param method a method to invoke
     * @param obj an object to run the {@code method} (can be null if static method)
     * @param args an argument passed to the {@code method} */
    protected void assumeSame(Object expected, Method method, Object obj, Object... args)
    { assumeSame(null, expected, method, obj, args); }

    /** When invoked using {@link java.lang.reflect.Method#invoke(Object, Object...)},
     * expect to return a reference to {@code expected} (referencing the same object)
     * @param message a string that gives information about this test / method calls
     * @param method a method to invoke
     * @param obj an object to run the {@code method} (can be null if static method)
     * @param args an argument passed to the {@code method} */
    protected void assumeSame(String message, Object expected, Method method, Object obj, Object... args)
    { record(message, Status.REFERENCE, expected, method, obj, args); }

    /** When invoked using {@link java.lang.reflect.Method#invoke(Object, Object...)},
     * expect to return an object which <strong>not</strong> referencing to {@code expected}
     * @param method a method to invoke
     * @param obj an object to run the {@code method} (can be null if static method)
     * @param args an argument passed to the {@code method} */
    protected void assumeNotSame(Object expected, Method method, Object obj, Object... args)
    { assumeNotSame(null, expected, method, obj, args); }

    /** When invoked using {@link java.lang.reflect.Method#invoke(Object, Object...)},
     * expect to return an object which <strong>not</strong> referencing to {@code expected}
     * @param message a string that gives information about this test / method calls
     * @param method a method to invoke
     * @param obj an object to run the {@code method} (can be null if static method)
     * @param args an argument passed to the {@code method} */
    protected void assumeNotSame(String message, Object expected, Method method, Object obj, Object... args)
    { record(message, Status.NOT_REFERENCE, expected, method, obj, args); }

    /** When invoked using {@link java.lang.reflect.Method#invoke(Object, Object...)},
     * expect to return {@code true}
     * @param method a method to invoke
     * @param obj an object to run the {@code method} (can be null if static method)
     * @param args an argument passed to the {@code method} */
    protected void assumeTrue(Method method, Object obj, Object... args)
    { assumeTrue(null, method, obj, args); }

    /** When invoked using {@link java.lang.reflect.Method#invoke(Object, Object...)},
     * expect to return {@code true}
     * @param message a string that gives information about this test / method calls
     * @param method a method to invoke
     * @param obj an object to run the {@code method} (can be null if static method)
     * @param args an argument passed to the {@code method} */
    protected void assumeTrue(String message, Method method, Object obj, Object... args)
    { record(message, Status.EQUAL, true, method, obj, args); }

    /** When invoked using {@link java.lang.reflect.Method#invoke(Object, Object...)},
     * expect to return {@code false}
     * @param method a method to invoke
     * @param obj an object to run the {@code method} (can be null if static method)
     * @param args an argument passed to the {@code method} */
    protected void assumeFalse(Method method, Object obj, Object... args)
    { assumeFalse(null, method, obj, args); }

    /** When invoked using {@link java.lang.reflect.Method#invoke(Object, Object...)},
     * expect to return {@code false}
     * @param message a string that gives information about this test / method calls
     * @param method a method to invoke
     * @param obj an object to run the {@code method} (can be null if static method)
     * @param args an argument passed to the {@code method} */
    protected void assumeFalse(String message, Method method, Object obj, Object... args)
    { record(message, Status.EQUAL, false, method, obj, args); }

    /** When invoked using {@link java.lang.reflect.Method#invoke(Object, Object...)},
     * expect to return an object equal to {@code expected} (determined by {@link java.lang.Object#equals(Object)})
     * @param method a method to invoke
     * @param obj an object to run the {@code method} (can be null if static method)
     * @param args an argument passed to the {@code method} */
    protected void assumeEquals(Object expected, Method method, Object obj, Object... args)
    { assumeEquals(null, expected, method, obj, args); }

    /** When invoked using {@link java.lang.reflect.Method#invoke(Object, Object...)},
     * expect to return an object equal to {@code expected} (determined by {@link java.lang.Object#equals(Object)})
     * @param message a string that gives information about this test / method calls
     * @param method a method to invoke
     * @param obj an object to run the {@code method} (can be null if static method)
     * @param args an argument passed to the {@code method} */
    protected void assumeEquals(String message, Object expected, Method method, Object obj, Object... args)
    { record(message, Status.EQUAL, expected, method, obj, args); }

    /** When invoked using {@link java.lang.reflect.Method#invoke(Object, Object...)},
     * expect to return an object <strong>not</strong> equal to {@code expected}
     * (determined by {@link java.lang.Object#equals(Object)})
     * @param method a method to invoke
     * @param obj an object to run the {@code method} (can be null if static method)
     * @param args an argument passed to the {@code method} */
    protected void assumeNotEquals(Object expected, Method method, Object obj, Object... args)
    { assumeNotEquals(null, expected, method, obj, args); }

    /** When invoked using {@link java.lang.reflect.Method#invoke(Object, Object...)},
     * expect to return an object <strong>not</strong> equal to {@code expected}
     * (determined by {@link java.lang.Object#equals(Object)})
     * @param message a string that gives information about this test / method calls
     * @param method a method to invoke
     * @param obj an object to run the {@code method} (can be null if static method)
     * @param args an argument passed to the {@code method} */
    protected void assumeNotEquals(String message, Object expected, Method method, Object obj, Object... args)
    { record(message, Status.NOT_EQUAL, expected, method, obj, args); }

    /** When invoked using {@link java.lang.reflect.Method#invoke(Object, Object...)},
     * expect to return an array of object. And when compared to {@code expected}, expect to have same length and
     * iteratively equal (determined by {@link java.lang.Object#equals(Object)})
     * @param method a method to invoke
     * @param obj an object to run the {@code method} (can be null if static method)
     * @param args an argument passed to the {@code method} */
    protected void assumeArrayEquals(Object[] expected, Method method, Object obj, Object... args)
    { assumeArrayEquals(null, expected, method, obj, args); }

    /** When invoked using {@link java.lang.reflect.Method#invoke(Object, Object...)},
     * expect to return an array of object. And when compared to {@code expected}, expect to have same length and
     * iteratively equal (determined by {@link java.lang.Object#equals(Object)})
     * @param message a string that gives information about this test / method calls
     * @param method a method to invoke
     * @param obj an object to run the {@code method} (can be null if static method)
     * @param args an argument passed to the {@code method} */
    protected void assumeArrayEquals(String message, Object[] expected, Method method, Object obj, Object... args)
    { record(message, Status.ARRAY_EQUAL, expected, method, obj, args); }

    /** When invoked using {@link java.lang.reflect.Method#invoke(Object, Object...)},
     * expect to return an array of object. And when compared to {@code expected}, expect either have different length
     * or <strong>not</strong> iteratively equal (determined by {@link java.lang.Object#equals(Object)})
     * @param method a method to invoke
     * @param obj an object to run the {@code method} (can be null if static method)
     * @param args an argument passed to the {@code method} */
    protected void assumeArrayNotEquals(Object[] expected, Method method, Object obj, Object... args)
    { assumeArrayNotEquals(null, expected, method, obj, args); }

    /** When invoked using {@link java.lang.reflect.Method#invoke(Object, Object...)},
     * expect to return an array of object. And when compared to {@code expected}, expect either have different length
     * or <strong>not</strong> iteratively equal (determined by {@link java.lang.Object#equals(Object)})
     * @param message a string that gives information about this test / method calls
     * @param method a method to invoke
     * @param obj an object to run the {@code method} (can be null if static method)
     * @param args an argument passed to the {@code method} */
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
