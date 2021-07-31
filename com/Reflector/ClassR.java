package com.Reflector;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ClassR
{
    private Class<?> mClass;
    public ClassR(Class<?> aClass) { mClass = aClass; }

    /** create an instance of a class */
    public Object newInstance(Object... args) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException
    {
        if (args.length == 0)
            return mClass.getConstructor().newInstance();
        return mClass.getConstructor(args.getClass()).newInstance(args);
    }

    /** call to static function */
    public Object invoke(String funcName, Object... args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {
        if (args.length == 0)
            return mClass.getMethod(funcName).invoke(null);
        return mClass.getMethod(funcName, args.getClass()).invoke(args);
    }

    /** call to a member function */
    public Object invoke(Object object, String funcName, Object... args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {
        if (args.length == 0)
            return mClass.getMethod(funcName).invoke(object);
        return mClass.getMethod(funcName, args.getClass()).invoke(object, args);
    }

}
