package com.NetlabUT;

import com.NetlabUT.annotations.*;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

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
            List<Object> tests               = null;
            Reflections reflections          = new Reflections(sourceClass.getPackageName());
            Set<Class<?>> internalTestSet    = reflections.getTypesAnnotatedWith(NetlabTest.class);
            Set<Class<?>> reflectTestSet     = reflections.getTypesAnnotatedWith(NetlabReflectTest.class);
            // if only contain internal test
            if (reflectTestSet.size() == 0)
            {
                tests = new ArrayList<>(internalTestSet.size());
                for (Class<?> testClass : internalTestSet)
                {
                    Object testUnit = testClass.getConstructor().newInstance();
                    tests.add(testUnit);
                }

                try { new SinglePackageProfiler(title, tests).setVisible(true); }
                catch (Throwable t)
                {
                    t.printStackTrace();
                    System.err.println(t);
                    System.exit(1);
                }
                return;
            }
            // else merge them so it can be displayed using MultiPackageProfiler
            String currentDir = System.getProperty("user.dir");
            File file = new File(currentDir + "/TestedPackages.txt");
            Scanner scanner = new Scanner(file);
            Set<Class<?>> classes = new HashSet<>(100);
            while (scanner.hasNextLine())
            {
                String lookupPath = scanner.nextLine();
                File dir = new File(lookupPath);
                for (Class<?> rt : reflectTestSet)
                {
                    if (!dir.isDirectory())
                    {
                        System.err.println(dir + " is not a directory");
                        continue;
                    }
                    NetlabReflectTest annotation = rt.getAnnotation(NetlabReflectTest.class);
                    File fileCode = new File(dir, annotation.value() + ".java");
                    if (!fileCode.exists())
                    {
                        System.err.println(fileCode + " is not exists");
                        continue;
                    }
                    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
                    int returnCompiler = compiler.run(null, null, null, fileCode.getAbsolutePath());
                    if (returnCompiler != 0)
                    {
                        String msg = "Java compile exit code: " + returnCompiler;
                        System.err.println(msg);
                        continue;
                    }
                    File fileClass = new File(dir, annotation.value() + ".class");
                    if (!fileClass.exists())
                    {
                        System.err.println(fileClass + " is not exists");
                        continue;
                    }
                    byte[] bytes = Files.readAllBytes(fileClass.toPath());
                    Class<?> clazz = new ByteClassLoader().defineClass(bytes);
                    classes.add(clazz);
                }
            }

            Function<String, Class<?>> getClass = (name) -> {
                for (Class<?> c : classes)
                    if (c.getSimpleName().equals(name))
                        return c;
                return null;
            };

            tests = new ArrayList<>(reflectTestSet.size());
            for (Class<?> rt : reflectTestSet)
            {
                Field[] reflectFields = rt.getDeclaredFields();
                Function<Class<? extends Annotation>, Set<Field>> getFieldsAnnotatedWith = (a) -> {
                    Set set = new HashSet<>();
                    for (Field f : reflectFields)
                        if (f.isAnnotationPresent(a))
                            set.add(f);
                    return set;
                };

                Set<Field> reflectField      = getFieldsAnnotatedWith.apply(ReflectField.class);
                Set<Field> reflectMethod     = getFieldsAnnotatedWith.apply(ReflectMethod.class);
                Set<Field> reflectCtor       = getFieldsAnnotatedWith.apply(ReflectCtor.class);
                NetlabReflectTest annotation = rt.getAnnotation(NetlabReflectTest.class);
                Class<?> targetClass         = getClass.apply(annotation.value());
                if (targetClass == null) continue;

                Constructor<?>[] classCtors  = targetClass.getDeclaredConstructors();
                Field[] classFields          = targetClass.getDeclaredFields();
                Method[] classMethods        = targetClass.getDeclaredMethods();

                Object unitTestInstance      = rt.getConstructor().newInstance();

                for (Field fField : reflectField)
                {
                    fField.setAccessible(true);
                    ReflectField annField = fField.getAnnotation(ReflectField.class);
                    String expectedName   = annField.value().isEmpty() ? fField.getName() : annField.value();
                    Field field           = Arrays.stream(classFields).filter(f ->
                            f.getName().equals(expectedName)).findFirst().orElse(null);
                    fField.set(unitTestInstance, field);
                }
                for (Field fMethod : reflectMethod)
                {
                    fMethod.setAccessible(true);
                    ReflectMethod annField    = fMethod.getAnnotation(ReflectMethod.class);
                    String expectedName       = annField.value().isEmpty() ? fMethod.getName() : annField.value();
                    Class<?>[] expectedParams = annField.params();
                    Method method             = Arrays.stream(classMethods).filter(m ->
                            m.getName().equals(expectedName) &&
                            Arrays.equals(expectedParams, m.getParameterTypes())).findFirst().orElse(null);
                    fMethod.set(unitTestInstance, method);
                }
                for (Field fCtor : reflectCtor)
                {
                    fCtor.setAccessible(true);
                    ReflectCtor annField      = fCtor.getAnnotation(ReflectCtor.class);
                    Class<?>[] expectedParams = annField.params();
                    Constructor<?> ctor       = Arrays.stream(classCtors).filter(c ->
                            Arrays.equals(expectedParams, c.getParameterTypes())).findFirst().orElse(null);
                    fCtor.set(unitTestInstance, ctor);
                }
                tests.add(unitTestInstance);
            }
            new MultiPackageProfiler(title, tests);
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