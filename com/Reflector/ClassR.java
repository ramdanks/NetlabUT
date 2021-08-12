package com.Reflector;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

/** Class type wrapper to suppress throwable
 * @author Ramadhan Kalih Sewu
 * @version 1.0
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

    /** get every throwable that happens when using a method from {@link ClassR} */
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

}