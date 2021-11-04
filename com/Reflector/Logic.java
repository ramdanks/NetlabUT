package com.Reflector;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

@Deprecated
public class Logic
{
    public static boolean isModifier(ReflectorModifier modifier, Class<?> clazz)
    {
        if (clazz == null) return false;
        switch (modifier)
        {
            case ENUM:          return clazz.isEnum();
            case MEMBER_CLASS:  return clazz.isMemberClass();
            case LOCAL_CLASS:   return clazz.isLocalClass();
            case SYNTHETIC:     return clazz.isSynthetic();
            case ANNOTATION:    return clazz.isAnnotation();
            default:            return isGeneralModifier(modifier, clazz.getModifiers());
        }
    }
    public static boolean isModifier(ReflectorModifier modifier, Method method)
    {
        if (method == null) return false;
        switch (modifier)
        {
            case DEFAULT:       return method.isDefault();
            case BRIDGE:        return method.isBridge();
            case VARARGS:       return method.isVarArgs();
            case SYNTHETIC:     return method.isSynthetic();
            default:            return isGeneralModifier(modifier, method.getModifiers());
        }
    }
    public static boolean isModifier(ReflectorModifier modifier, Constructor<?> constructor)
    {
        if (constructor == null) return false;
        switch (modifier)
        {
            case SYNTHETIC:     return constructor.isSynthetic();
            case VARARGS:       return constructor.isVarArgs();
            default:            return isGeneralModifier(modifier, constructor.getModifiers());
        }
    }
    public static boolean isModifier(ReflectorModifier modifier, Field field)
    {
        if (field == null) return false;
        switch (modifier)
        {
            case SYNTHETIC:     return field.isSynthetic();
            case ENUM:          return field.isEnumConstant();
            default:            return isGeneralModifier(modifier, field.getModifiers());
        }
    }

    private static boolean isGeneralModifier(ReflectorModifier modifier, int reflectModifierFlag)
    {
        switch (modifier)
        {
            case PUBLIC:        return Modifier.isPublic(reflectModifierFlag);
            case PRIVATE:       return Modifier.isPrivate(reflectModifierFlag);
            case PROTECTED:     return Modifier.isProtected(reflectModifierFlag);
            case STATIC:        return Modifier.isStatic(reflectModifierFlag);
            case FINAL:         return Modifier.isFinal(reflectModifierFlag);
            case SYNCHRONIZED:  return Modifier.isSynchronized(reflectModifierFlag);
            case VOLATILE:      return Modifier.isVolatile(reflectModifierFlag);
            case TRANSIENT:     return Modifier.isTransient(reflectModifierFlag);
            case NATIVE:        return Modifier.isNative(reflectModifierFlag);
            case INTERFACE:     return Modifier.isInterface(reflectModifierFlag);
            case ABSTRACT:      return Modifier.isAbstract(reflectModifierFlag);
            case STRICT:        return Modifier.isStrict(reflectModifierFlag);
            default:            return false;
        }
    }
}