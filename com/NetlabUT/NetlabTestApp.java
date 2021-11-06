package com.NetlabUT;

import com.NetlabUT.annotations.*;
import org.reflections.Reflections;

import javax.swing.*;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Consumer;

/** provides a driver to run NetlabTest
 * @author Ramadhan Kalih Sewu
 * @version 2.0
 */
public class NetlabTestApp
{
    public static class ByteClassLoader extends ClassLoader
    {
        public Class<?> defineClass(byte[] classBytes)
        {
            return defineClass(null, classBytes, 0, classBytes.length);
        }
    }

    public static void runReflect(Class<?> sourceClass, String title, String[] packageNames)
    {
        Set<Class<?>> tests = getNetlabReflectTest(sourceClass.getPackageName());
        Class<? extends NetlabReflectTest>[] unitTests = (Class<? extends NetlabReflectTest>[]) tests.toArray(new Class[0]);

        if (unitTests.length == 0 || packageNames == null || packageNames.length == 0)
            return;

        HashMap<String, List<Object>> mapPackageUT = new HashMap<>();

        Arrays.stream(packageNames).forEach(pkg -> { try {
            HashMap<String, Class<?>> subjectClasses = new HashMap<>();
            for (Class<?> ut : unitTests)
            {
                for (Field f : ut.getDeclaredFields())
                {
                    // find needed class from all annotation presents
                    String className     = "";
                    if (f.isAnnotationPresent(ReflectClass.class))
                    {
                        String avalue = f.getAnnotation(ReflectClass.class).value();
                        className     = avalue.isEmpty() ? f.getName() : avalue;
                    }
                    else if (f.isAnnotationPresent(ReflectField.class))
                        className = f.getAnnotation(ReflectField.class).owner();
                    else if (f.isAnnotationPresent(ReflectMethod.class))
                        className = f.getAnnotation(ReflectMethod.class).owner();
                    else if (f.isAnnotationPresent(ReflectCtor.class))
                        className = f.getAnnotation(ReflectCtor.class).owner();

                    if (className == null || className.isBlank())
                        continue;

                    try
                    {
                        Class<?> target = Class.forName(pkg + "." + className);
                        subjectClasses.put(className, target);
                    }
                    catch (ClassNotFoundException e)  { System.err.println(e); }
                }
            }

            // now set the rest of ReflectKind
            List<Object> unitInstances = new ArrayList<>(unitTests.length);
            for (Class<?> ut : unitTests)
            {
                // HOT: potentially throws an exception because NetlabTest has no default ctor
                NetlabReflectTest ann = ut.getAnnotation(NetlabReflectTest.class);
                Object utInstance = ut.getDeclaredConstructor().newInstance();
                unitInstances.add(utInstance);
                for (Field f : ut.getDeclaredFields())
                {
                    // if fail to set, then set field to null (ignored)

                    Consumer<Executable<AccessibleObject>> setReflectOther = (e) -> { try {
                        AccessibleObject ao = e.execute();
                        if (ann.makeAccessible())
                            ao.setAccessible(true);
                        f.setAccessible(true);
                        f.set(utInstance, ao);
                    } catch (Throwable ignored) {}
                    };

                    Consumer<Executable<Class<?>>> setReflectClass = (e) -> { try {
                        f.setAccessible(true);
                        f.set(utInstance, e.execute());
                    } catch (Throwable ignored) {}
                    };

                    if (f.isAnnotationPresent(ReflectClass.class))
                    {
                        ReflectClass annField = f.getAnnotation(ReflectClass.class);
                        String expectedName   = annField.value().isEmpty() ? f.getName() : annField.value();
                        Class<?> subjectClass = subjectClasses.get(expectedName);
                        if (subjectClass == null) continue;
                        setReflectClass.accept(() -> subjectClass);
                    }
                    else if (f.isAnnotationPresent(ReflectField.class))
                    {
                        ReflectField annField = f.getAnnotation(ReflectField.class);
                        String owner          = annField.owner();
                        Class<?> subjectClass = subjectClasses.get(owner);
                        if (subjectClass == null) continue;
                        String expectedName   = annField.value().isEmpty() ? f.getName() : annField.value();
                        setReflectOther.accept(() -> subjectClass.getDeclaredField(expectedName));
                    }
                    else if (f.isAnnotationPresent(ReflectCtor.class))
                    {
                        ReflectCtor annField    = f.getAnnotation(ReflectCtor.class);
                        String owner            = annField.owner();
                        Class<?> subjectClass   = subjectClasses.get(owner);
                        if (subjectClass == null) continue;
                        Class<?>[] expectedParams = annField.params();
                        setReflectOther.accept(() -> subjectClass.getDeclaredConstructor(expectedParams));
                    }
                    else if (f.isAnnotationPresent(ReflectMethod.class))
                    {
                        ReflectMethod annField    = f.getAnnotation(ReflectMethod.class);
                        String owner              = annField.owner();
                        Class<?> subjectClass     = subjectClasses.get(owner);
                        if (subjectClass == null) continue;
                        String expectedName       = annField.value().isEmpty() ? f.getName() : annField.value();
                        Class<?>[] expectedParams = annField.params();
                        setReflectOther.accept(() -> subjectClass.getDeclaredMethod(expectedName, expectedParams));
                    }
                }
            }
            mapPackageUT.put(pkg, unitInstances);
        } catch (Throwable t) { t.printStackTrace(); }
        });

        PackageWindowGrading wnd = new PackageWindowGrading(title, mapPackageUT);
        wnd.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        wnd.setVisible(true);
        wnd.runTester();
    }

    public static void runReflect(Class<?> sourceClass, String title)
    {
        runReflect(sourceClass, title, new String[]{sourceClass.getPackageName()});
    }

    public static void run(Class<? extends NetlabTest>[] unitTests, String title)
    {
        if (unitTests == null || unitTests.length == 0)
            return;
        try
        {
            List<Object> tests = new ArrayList<>(unitTests.length);
            for (Class<?> ut : unitTests)
            {
                // HOT: potentially throws an exception because NetlabTest has no default ctor
                Object testUnit = ut.getConstructor().newInstance();
                tests.add(testUnit);
            }
            new WindowGrading(title, tests).setVisible(true);
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            System.err.println(t);
            System.exit(1);
        }
    }

    public static void run(Class<?> sourceClass, String title)
    {
        Set<Class<?>> tests = getNetlabTest(sourceClass.getPackageName());
        Class<? extends NetlabTest>[] unitTests = (Class<? extends NetlabTest>[]) tests.toArray(new Class[0]);
        run(unitTests, title);
    }

    public static Set<Class<?>> getNetlabTest(String packageName)
    {
        Reflections reflections = new Reflections(packageName);
        return reflections.getTypesAnnotatedWith(NetlabTest.class);
    }

    public static Set<Class<?>> getNetlabReflectTest(String packageName)
    {
        Reflections reflections = new Reflections(packageName);
        return reflections.getTypesAnnotatedWith(NetlabReflectTest.class);
    }
}