package com.NetlabUT;

import com.NetlabUT.annotations.*;
import org.reflections.Reflections;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Consumer;

public class NetlabTestApp
{
    public static class ByteClassLoader extends ClassLoader
    {
        public Class<?> defineClass(byte[] classBytes)
        {
            return defineClass(null, classBytes, 0, classBytes.length);
        }
    }

    public static void run(Class<?> sourceClass, String title)
    {
        try
        {
            List<Object> tests          = null;
            boolean reflectKindPresent  = false;
            Reflections reflections     = new Reflections(sourceClass.getPackageName());
            Set<Class<?>> testClasses   = reflections.getTypesAnnotatedWith(NetlabTest.class);
            if (testClasses.size() == 0)
            {
                System.out.println("No NetlabTest detected in " + sourceClass.getPackageName());
                return;
            }
            // TODO: replace with elegant way to use meta annotations, find by ReflectKind.class
            FIND_REFLECT_KIND:
            for (Class<?> ut : testClasses)
            {
                for (Field f : ut.getDeclaredFields())
                {
                    if (f.isAnnotationPresent(ReflectClass.class) ||
                        f.isAnnotationPresent(ReflectMethod.class) ||
                        f.isAnnotationPresent(ReflectField.class) ||
                        f.isAnnotationPresent(ReflectCtor.class))
                        reflectKindPresent = true;
                        break FIND_REFLECT_KIND;
                }
            }
            // run in the current environment
            if (!reflectKindPresent)
            {
                tests = new ArrayList<>(testClasses.size());
                for (Class<?> ut : testClasses)
                {
                    Object testUnit = ut.getConstructor().newInstance();
                    tests.add(testUnit);
                }
                try { new WindowGrading(title, tests).setVisible(true); }
                catch (Throwable t)
                {
                    t.printStackTrace();
                    System.err.println(t);
                    System.exit(1);
                }
                return;
            }
            // reflect test for every package tested
            String currentDir = System.getProperty("user.dir");
            File file = new File(currentDir + "/TestedPackages.txt");
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine())
            {
                File lookupDirPath = new File(scanner.nextLine());
                if (!lookupDirPath.isDirectory())
                {
                    System.err.println(lookupDirPath + " is not a directory");
                    continue;
                }
                // find all classes reference in NetlabTest to compile
                HashMap<String, Class<?>> subjectClasses = new HashMap<>();
                for (Class<?> ut : testClasses)
                {
                    for (Field f : ut.getDeclaredFields())
                    {
                        // find needed class from all annotation presents
                        String className     = "";
                        if (f.isAnnotationPresent(ReflectClass.class))
                            className = f.getAnnotation(ReflectClass.class).value();
                        else if (f.isAnnotationPresent(ReflectField.class))
                            className = f.getAnnotation(ReflectField.class).owner();
                        else if (f.isAnnotationPresent(ReflectMethod.class))
                            className = f.getAnnotation(ReflectMethod.class).owner();
                        else if (f.isAnnotationPresent(ReflectCtor.class))
                            className = f.getAnnotation(ReflectCtor.class).owner();
                        // invalid value for class type
                        if (className == null || className.isEmpty())
                            continue;
                        // no need to recompile
                        if (subjectClasses.get(className) != null)
                            continue;
                        // proceed to find a source code with .java extension
                        File fileCode  = new File(lookupDirPath, className + ".java");
                        File fileClass = new File(lookupDirPath, className + ".class");
                        if (!fileCode.exists())
                        {
                            System.err.println(fileCode + " is not exists");
                            continue;
                        }
                        // compile the .java code
                        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
                        int returnCompiler    = compiler.run(null, null, null, fileCode.getAbsolutePath());
                        if (returnCompiler != 0)
                        {
                            String msg = "Java compile exit code: " + returnCompiler;
                            System.err.println(msg);
                            continue;
                        }
                        // it should produce a file with the same name but with .class extension
                        if (!fileClass.exists())
                        {
                            System.err.println(fileClass + " is not exists");
                            continue;
                        }
                        // load class from byte[]
                        byte[] bytes = Files.readAllBytes(fileClass.toPath());
                        Class<?> clazz = new ByteClassLoader().defineClass(bytes);
                        subjectClasses.put(className, clazz);
                    }
                }
                // now set the rest of ReflectKind
                tests = new ArrayList<>(testClasses.size());
                for (Class<?> ut : testClasses)
                {
                    Object utInstance = ut.getDeclaredConstructor().newInstance();
                    for (Field f : ut.getDeclaredFields())
                    {
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
                            if (subjectClass == null)
                                continue;
                            setReflectField.accept(() -> subjectClass);
                        }
                        else if (f.isAnnotationPresent(ReflectField.class))
                        {
                            ReflectField annField = f.getAnnotation(ReflectField.class);
                            String owner          = annField.owner();
                            Class<?> subjectClass = subjectClasses.get(owner);
                            if (subjectClass == null)
                                continue;
                            String expectedName   = annField.value().isEmpty() ? f.getName() : annField.value();
                            setReflectField.accept(() -> subjectClass.getDeclaredField(expectedName));
                        }
                        else if (f.isAnnotationPresent(ReflectCtor.class))
                        {
                            ReflectCtor annField    = f.getAnnotation(ReflectCtor.class);
                            String owner            = annField.owner();
                            Class<?> subjectClass   = subjectClasses.get(owner);
                            if (subjectClass == null)
                                continue;
                            Class<?>[] expectedParams = annField.params();
                            setReflectField.accept(() -> subjectClass.getDeclaredConstructor(expectedParams));
                        }
                        else if (f.isAnnotationPresent(ReflectMethod.class))
                        {
                            ReflectMethod annField    = f.getAnnotation(ReflectMethod.class);
                            String owner              = annField.owner();
                            Class<?> subjectClass     = subjectClasses.get(owner);
                            if (subjectClass == null)
                                continue;
                            String expectedName       = annField.value().isEmpty() ? f.getName() : annField.value();
                            Class<?>[] expectedParams = annField.params();
                            setReflectField.accept(() -> subjectClass.getDeclaredMethod(expectedName, expectedParams));
                        }
                    }
                    tests.add(utInstance);
                }
                new MultiPackageProfiler(title, tests);
            }
        }
        catch (IOException exception)
        {
            exception.printStackTrace();
            System.err.println(exception);
            System.exit(2);
        }
        catch (Throwable throwable)
        {
            throwable.printStackTrace();
            System.err.println(throwable);
            System.exit(3);
        }
    }
}