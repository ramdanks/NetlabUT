package com.Reflector;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/** Class type wrapper to suppress throwable
 * @author Ramadhan Kalih Sewu
 * @version 1.0
 */
public class ClassR
{
    private Class<?> mClass;

    public ClassR(Class<?> aClass) { mClass = aClass; }
    public ClassR (String packageName, String className)
    {
        try {
            if (packageName == null || packageName.isEmpty())
                mClass = Class.forName(className);
            mClass = Class.forName(packageName + '.' + className); }
        catch (Throwable ignored) {}
    }
    public ClassR(String className)
    {
        try { mClass = Class.forName(className); }
        catch (Throwable ignored) {}
    }

    public Class<?> getContainingClass() { return mClass; }

    /** create an instance of a class using default constructor */
    public Object newInstance()
    {
        try { return mClass.getConstructor().newInstance(); }
        catch (Throwable ignored) {}
        return null;
    }

    public Object newInstance(Constructor<?> constructor, Object... args)
    {
        try { return constructor.newInstance(args); }
        catch (Throwable ignored) {}
        return null;
    }

    public Constructor<?> getConstructor(Class<?>... paramTypes)
    {
        try { return mClass.getConstructor(paramTypes); }
        catch (Throwable ignored) {}
        return null;
    }

    public Method getMethod(String funcName, Class<?>... paramTypes)
    {
        try { return mClass.getMethod(funcName, paramTypes); }
        catch (Throwable ignored) {}
        return null;
    }

    public Field getField(String name)
    {
        try { return mClass.getField(name); }
        catch (Throwable ignored) {}
        return null;
    }

    public Field getDeclaredField(String name)
    {
        try { return mClass.getDeclaredField(name); }
        catch (Throwable ignored) {}
        return null;
    }

    public boolean setField(Field field, Object object, Object value)
    {
        try { field.set(object, value); }
        catch (Throwable ignored) { return false; }
        return true;
    }

    public boolean setFieldForce(Field field, Object object, Object value)
    {
        try {
            field.setAccessible(true);
            field.set(object, value);
        } catch (Throwable ignored) { return false; }
        return true;
    }

}