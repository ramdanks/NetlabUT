package com.NetlabUT;

import com.NetlabUT.annotations.*;
import org.reflections.Reflections;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Consumer;

/** provides a driver to run NetlabTest
 * @author Ramadhan Kalih Sewu
 * @version 1.3
 */
public class NetlabTestApp
{
    public static final int SUBJECT_JAVA_ONLY = 0;
    public static final int SUBJECT_CLASS_ONLY = 1;
    public static final int SUBJECT_PRIORITIZE_JAVA = 2;
    public static final int SUBJECT_PRIORITIZE_CLASS = 3;

    public static class ByteClassLoader extends ClassLoader
    {
        public Class<?> defineClass(byte[] classBytes)
        {
            return defineClass(null, classBytes, 0, classBytes.length);
        }
    }

    public static void run(Class<? extends NetlabTest>[] unitTests, String title, File[] subjectDirectories, int subjectHook)
    {
        try
        {
            // iterate through all subject dirs
            for (File subjectDir : subjectDirectories)
            {
                if (!subjectDir.isDirectory())
                {
                    System.err.println(subjectDir + " is not a directory");
                    continue;
                }
                // find all classes reference in NetlabTest to compile
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

                        // invalid value for class type and already compiled
                        if (className == null || className.isEmpty() || subjectClasses.get(className) != null)
                            continue;

                        // proceed to find a source code with .java extension
                        File fileCode  = new File(subjectDir, className + ".java");
                        File fileClass = new File(subjectDir, className + ".class");

                        boolean shouldCompletelyCompiled =
                            subjectHook == SUBJECT_JAVA_ONLY ||
                            subjectHook == SUBJECT_PRIORITIZE_CLASS && !fileClass.exists() ||
                            subjectHook == SUBJECT_PRIORITIZE_JAVA && fileCode.exists();

                        if (shouldCompletelyCompiled)
                        {
                            if (!fileCode.exists())
                            {
                                System.err.println(fileCode + " is not exists");
                                continue;
                            }
                            if (fileClass.exists() && !fileClass.delete())
                            {
                                System.err.println("Fail to delete " + fileClass + " prior to compile .java");
                                continue;
                            }
                            // compile the .java code
                            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
                            int returnCompiler    = compiler.run(null, null, null, fileCode.getAbsolutePath());
                            if (returnCompiler != 0)
                            {
                                String msg = "Java compile exit code: " + returnCompiler;
                                System.err.println(msg);
                                break;
                            }
                        }
                        // at the end, load the compiled code or existing byte code
                        if (!fileClass.exists())
                        {
                            System.err.println(fileClass + " is not exists");
                            break;
                        }
                        // load class from byte[]
                        // HOT: potentially throws an IOException
                        byte[] bytes = Files.readAllBytes(fileClass.toPath());
                        Class<?> clazz = new ByteClassLoader().defineClass(bytes);
                        subjectClasses.put(className, clazz);
                    }
                }

                // now set the rest of ReflectKind
                List<Object> unitInstances = new ArrayList<>(unitTests.length);
                for (Class<?> ut : unitTests)
                {
                    // HOT: potentially throws an exception because NetlabTest has no default ctor
                    Object utInstance = ut.getDeclaredConstructor().newInstance();
                    unitInstances.add(utInstance);
                    for (Field f : ut.getDeclaredFields())
                    {
                        // if fail to set, then set field to null (ignored)
                        Consumer<Executable> setReflectField = (e) -> { try {
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
                            setReflectField.accept(() -> subjectClass);
                        }
                        else if (f.isAnnotationPresent(ReflectField.class))
                        {
                            ReflectField annField = f.getAnnotation(ReflectField.class);
                            String owner          = annField.owner();
                            Class<?> subjectClass = subjectClasses.get(owner);
                            if (subjectClass == null) continue;
                            String expectedName   = annField.value().isEmpty() ? f.getName() : annField.value();
                            setReflectField.accept(() -> subjectClass.getDeclaredField(expectedName));
                        }
                        else if (f.isAnnotationPresent(ReflectCtor.class))
                        {
                            ReflectCtor annField    = f.getAnnotation(ReflectCtor.class);
                            String owner            = annField.owner();
                            Class<?> subjectClass   = subjectClasses.get(owner);
                            if (subjectClass == null) continue;
                            Class<?>[] expectedParams = annField.params();
                            setReflectField.accept(() -> subjectClass.getDeclaredConstructor(expectedParams));
                        }
                        else if (f.isAnnotationPresent(ReflectMethod.class))
                        {
                            ReflectMethod annField    = f.getAnnotation(ReflectMethod.class);
                            String owner              = annField.owner();
                            Class<?> subjectClass     = subjectClasses.get(owner);
                            if (subjectClass == null) continue;
                            String expectedName       = annField.value().isEmpty() ? f.getName() : annField.value();
                            Class<?>[] expectedParams = annField.params();
                            setReflectField.accept(() -> subjectClass.getDeclaredMethod(expectedName, expectedParams));
                        }
                    }
                }
                new WindowGrading(title, unitInstances);
            }
        } catch (Throwable throwable)
        {
            throwable.printStackTrace();
            System.err.println(throwable);
            System.exit(1);
        }
    }

    public static void run(Class<?> sourceClass, String title, File[] subjectDirectories, int subjectHook)
    {
        Set<Class<?>> tests = getNetlabTest(sourceClass.getPackageName());
        if (tests.size() == 0)
        {
            System.out.println("No NetlabTest detected in " + sourceClass.getPackageName());
            return;
        }
        Class<? extends NetlabTest>[] unitTests = (Class<? extends NetlabTest>[]) tests.toArray();
        run(unitTests, title, subjectDirectories, subjectHook);
    }

    public static void run(Class<? extends NetlabTest>[] unitTests, String title)
    {
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
        if (tests.size() == 0)
        {
            System.out.println("No NetlabTest detected in " + sourceClass.getPackageName());
            return;
        }
        Class<? extends NetlabTest>[] unitTests = (Class<? extends NetlabTest>[]) tests.toArray();
        run(unitTests, title);
    }

    public static Set<Class<?>> getNetlabTest(String packageName)
    {
        Reflections reflections = new Reflections(packageName);
        return reflections.getTypesAnnotatedWith(NetlabTest.class);
    }
}