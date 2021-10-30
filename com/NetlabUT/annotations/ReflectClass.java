package com.NetlabUT.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@ReflectKind
public @interface ReflectClass
{
    /** specify a class name */
    String value() default "";
}