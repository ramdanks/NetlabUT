package com.Reflector;
import sun.reflect.ReflectionFactory;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashMap;

/** Class type wrapper to interface with {@code java.lang.reflect}. Almost every method declared in this class
 * is suppressing {@link java.lang.Throwable}. it will return {@code null} on fail attempt.
 * @author Ramadhan Kalih Sewu
 * @version 1.2
 */
public class ClassR
{
    private Class<?> mClass;
    private ArrayList<Throwable> mThrowableList = new ArrayList<>();

    /** assign containing class */
    public ClassR(Class<?> aClass) { mClass = aClass; }
    /** find a requested class from {@link Class#forName(String)}. This will concatenate {@code packageName}
     * with {@code className} with '.' if packageName is not {@code null} */
    public ClassR (String packageName, String className)
    {
        try
        {
            mClass = packageName == null || packageName.isEmpty() ?
                Class.forName(className) :
                Class.forName(packageName + '.' + className);
        }
        catch (Throwable t) { mThrowableList.add(t); }
    }
    /** find a requested class from {@link Class#forName(String)} */
    public ClassR(String className)
    {
        try { mClass = Class.forName(className); }
        catch (Throwable t) { mThrowableList.add(t); }
    }

    /** get Class that is contained in this */
    public Class<?> getContainingClass() { return mClass; }

    /** get every throwable that was suppressed by this class except for
     * {@link ClassR#getForceAccess(Method, Object)} */
    public ArrayList<Throwable> getThrowableList() { return mThrowableList; }

    /** create an instance of a class using default constructor */
    public Object newInstance()
    {
        try { return mClass.getConstructor().newInstance(); }
        catch (Throwable t) { mThrowableList.add(t); }
        return null;
    }

    /** create an instance from specified constructor
     * @return Object constructed from the {@code constructor} or
     * null if {@code constructor} is null or {@code args} doesn't satisfy */
    public Object newInstance(Constructor<?> constructor, Object... args)
    {
        try { return constructor.newInstance(args); }
        catch (Throwable t) { mThrowableList.add(t); }
        return null;
    }

    /** find a constructor using {@link Class#getConstructor(Class[])}
     * @return Constructor that is requested or null if not found or experience Throwable */
    public Constructor<?> getConstructor(Class<?>... paramTypes)
    {
        try { return mClass.getConstructor(paramTypes); }
        catch (Throwable t) { mThrowableList.add(t); }
        return null;
    }

    /** find a constructor using {@link Class#getDeclaredConstructor(Class[])}
     * @return Constructor that is requested or null if not found or experience Throwable */
    public Constructor<?> getDeclaredConstructor(Class<?>... paramTypes)
    {
        try { return mClass.getDeclaredConstructor(paramTypes); }
        catch (Throwable t) { mThrowableList.add(t); }
        return null;
    }

    /** find a method using {@link Class#getMethod(String, Class[])}
     * @return Method that is requested or null if not found or experience Throwable */
    public Method getMethod(String funcName, Class<?>... paramTypes)
    {
        try { return mClass.getMethod(funcName, paramTypes); }
        catch (Throwable t) { mThrowableList.add(t); }
        return null;
    }

    /** find a method using {@link Class#getDeclaredMethod(String, Class[])}
     * @return Method that is requested or null if not found or experience Throwable */
    public Method getDeclaredMethod(String funcName, Class<?>... paramTypes)
    {
        try { return mClass.getDeclaredMethod(funcName, paramTypes); }
        catch (Throwable t) { mThrowableList.add(t); }
        return null;
    }

    /** find a field using {@link Class#getField(String)}
     * @return Field that is requested or null if not found or experience Throwable */
    public Field getField(String name)
    {
        try { return mClass.getField(name); }
        catch (Throwable t) { mThrowableList.add(t); }
        return null;
    }

    /** find a field using {@link Class#getDeclaredField(String)}
     * @return Field that is requested or null if not found or experience Throwable */
    public Field getDeclaredField(String name)
    {
        try { return mClass.getDeclaredField(name); }
        catch (Throwable t) { mThrowableList.add(t); }
        return null;
    }

    /** set field using {@link Field#set(Object, Object)}
     * @return true if success false otherwise */
    public boolean setField(Field field, Object object, Object value)
    {
        try { field.set(object, value); }
        catch (Throwable t)
        {
            mThrowableList.add(t);
            return false;
        }
        return true;
    }

    /** set field using {@link Field#set(Object, Object)}. This will also make field accessible
     * for the future by using {@link Field#setAccessible(boolean)}.
     * @return true if success false otherwise */
    public boolean setFieldForce(Field field, Object object, Object value)
    {
        try
        {
            field.setAccessible(true);
            field.set(object, value);
        }
        catch (Throwable t)
        {
            mThrowableList.add(t);
            return false;
        }
        return true;
    }

    /** This will try to find an object that has access to call {@code method}.
     * If {@code method} is instance method (not static) and the given {@code obj} is null,
     * it will try to create an instance of a class that declares the {@code method}.
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
        {
            final Class<?> clazz = method.getDeclaringClass();
            final ReflectionFactory reflection = ReflectionFactory.getReflectionFactory();
            final Constructor<?> constructor = reflection.newConstructorForSerialization(
                    clazz, Object.class.getDeclaredConstructor(new Class[0]));
            obj = constructor.newInstance(new Object[0]);
        }
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