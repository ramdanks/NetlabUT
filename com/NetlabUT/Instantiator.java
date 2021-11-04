package com.NetlabUT;
import sun.reflect.ReflectionFactory;

import java.lang.reflect.*;
import java.util.ArrayList;

/** Class type wrapper to interface with {@code java.lang.reflect}. Almost every method declared in this class
 * is suppressing {@link java.lang.Throwable}. it will return {@code null} on fail attempt.
 * @author Ramadhan Kalih Sewu
 * @version 2.0
 */
public class Instantiator
{
    private Instantiator() {}

    /** create instantiation of the{@code clazz} using default constructor.
     * it will force the constructor to accessible and then call it. This helps to create
     * an instantiation even though the ctor was declared private, protected, or package isn't accessible. */
    public static <T> T getDefaultInstantiation(Class<T> clazz)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException
    {
        Constructor<T> constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        return (T) constructor.newInstance();
    }

    /** create an instantiation without calling the constructor */
    @SuppressWarnings("unchecked")
    public static <T> T getBypassInstantiation(Class<T> clazz)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException
    {
        final ReflectionFactory reflection = ReflectionFactory.getReflectionFactory();
        final Constructor<?> constructor = reflection.newConstructorForSerialization(clazz, Object.class.getDeclaredConstructor());
        return (T) constructor.newInstance(new Object[0]);
    }

    /** This will try to find an object that has access to call {@code method}.
     * If {@code method} is instance method (not static) and the given {@code obj} is null, it will try to create
     * an instance from the declaring class of {@code method} using {@link Instantiator#getBypassInstantiation(Class)}.
     * After that, it will check the given {@code obj} or the instance that was previously constructed
     * does have access to {@code method}. If it doesn't, it will make your {@code method} accessible
     * using {@link java.lang.reflect.Method#setAccessible(boolean)}.
     * @param method Method that needs an access
     * @param obj Object that tries to invoke the {@code method}. (can be null)
     * @return an Object that may have access to {@code method}. It may return the given {@code obj}
     * or return a new instantiation of the declaring class from {@code method}.
     * @throws InvocationTargetException may happen when try to create an instance, triggered by: {@link Constructor#newInstance(Object...)}
     * @throws InstantiationException may happen when try to create an instance, triggered by: {@link Constructor#newInstance(Object...)}
     * @throws IllegalAccessException may happen when try to create an instance, triggered by: {@link Constructor#newInstance(Object...)}
     * @throws NoSuchMethodException if the given {@code method} is null or
     * may happen when try to create an instance, triggered by: {@link Class#getDeclaredConstructor(Class[])}
     */
    public static Object getForceAccess(Method method, Object obj)
            throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException
    {
        if (method == null) throw new NoSuchMethodException("the given Method can not be null");
        // if static method, we need an object
        if (obj == null && !Modifier.isStatic(method.getModifiers()))
            obj = getBypassInstantiation(method.getDeclaringClass());
        // ask if obj can have access to method
        boolean objectCanAccess = false;
        try { objectCanAccess = method.canAccess(obj); }
        catch (IllegalArgumentException ignored) {}
        // force set accessible if obj doesn't have access to method
        if (!objectCanAccess)
            method.setAccessible(true);
        return obj;
    }
}