package com.NetlabUT.annotations;

import com.NetlabUT.Pair;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Set;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({
    "com.NetlabUT.annotations.ReflectClass",
    "com.NetlabUT.annotations.ReflectCtor",
    "com.NetlabUT.annotations.ReflectField",
    "com.NetlabUT.annotations.ReflectMethod",
})

public class ReflectProcessor extends AbstractProcessor
{
    public static final HashMap<String, String> REFLECT_FIELD_TYPE;

    static
    {
        REFLECT_FIELD_TYPE = new HashMap<>(4);
        REFLECT_FIELD_TYPE.put(ReflectClass.class.getName(), Class.class.getName());
        REFLECT_FIELD_TYPE.put(ReflectCtor.class.getName(), Constructor.class.getName());
        REFLECT_FIELD_TYPE.put(ReflectField.class.getName(), Field.class.getName());
        REFLECT_FIELD_TYPE.put(ReflectMethod.class.getName(), Method.class.getName());
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment)
    {
        for (TypeElement te : set)
        {
            String annotationType  = te.asType().toString();
            String expectedFieldType = REFLECT_FIELD_TYPE.get(annotationType);
            Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(te);
            for (Element e : elements)
            {
                String fieldType = e.asType().toString();
                if (!fieldType.equals(expectedFieldType))
                {
                    String msg = String.format(
                        "%s should be type of %s, incompatible type %s",
                        annotationType, expectedFieldType, fieldType);
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, msg, e);
                }
            }
        }
        return true;
    }
}
