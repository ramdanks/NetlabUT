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

    public ClassR(Class<?> aClass) { mClass = aClass; }
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
    public ClassR(String className)
    {
        try { mClass = Class.forName(className); }
        catch (Throwable t) { mThrowableList.add(t); }
    }

    public Class<?> getContainingClass() { return mClass; }

    public ArrayList<Throwable> getThrowableList() { return mThrowableList; }

    /** create an instance of a class using default constructor */
    public Object newInstance()
    {
        try { return mClass.getConstructor().newInstance(); }
        catch (Throwable t) { mThrowableList.add(t); }
        return null;
    }

    public Object newInstance(Constructor<?> constructor, Object... args)
    {
        try { return constructor.newInstance(args); }
        catch (Throwable t) { mThrowableList.add(t); }
        return null;
    }

    public Constructor<?> getConstructor(Class<?>... paramTypes)
    {
        try { return mClass.getConstructor(paramTypes); }
        catch (Throwable t) { mThrowableList.add(t); }
        return null;
    }

    public Method getMethod(String funcName, Class<?>... paramTypes)
    {
        try { return mClass.getMethod(funcName, paramTypes); }
        catch (Throwable t) { mThrowableList.add(t); }
        return null;
    }

    public Method getDeclaredMethod(String funcName, Class<?>... paramTypes)
    {
        try { return mClass.getDeclaredMethod(funcName, paramTypes); }
        catch (Throwable t) { mThrowableList.add(t); }
        return null;
    }

    public Field getField(String name)
    {
        try { return mClass.getField(name); }
        catch (Throwable t) { mThrowableList.add(t); }
        return null;
    }

    public Field getDeclaredField(String name)
    {
        try { return mClass.getDeclaredField(name); }
        catch (Throwable t) { mThrowableList.add(t); }
        return null;
    }

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