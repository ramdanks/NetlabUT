package com.Reflector;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;

/** Class type wrapper to suppress throwable */
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
        catch (Throwable t) {}
    }
    public ClassR(String className)
    {
        try { mClass = Class.forName(className); }
        catch (Throwable t) {}
    }

    public Class<?> getContainingClass() { return mClass; }

    /** create an instance of a class using default constructor */
    public Object newInstance()
    {
        try { return mClass.getConstructor().newInstance(); }
        catch (Throwable t) {}
        return null;
    }

    public Object newInstance(Constructor<?> constructor, Object... args)
    {
        try { return constructor.newInstance(args); }
        catch (Throwable t) {}
        return null;
    }

    public Constructor<?> getConstructor(Class<?>... paramTypes)
    {
        try { return mClass.getConstructor(paramTypes); }
        catch (Throwable t) {}
        return null;
    }

    public Method getMethod(String funcName, Class<?>... paramTypes)
    {
        try { return mClass.getMethod(funcName, paramTypes); }
        catch (Throwable t) {}
        return null;
    }

}
