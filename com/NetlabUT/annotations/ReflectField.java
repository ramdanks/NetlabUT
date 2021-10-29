package com.NetlabUT.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ReflectField
{
	/** specify a field name */
	String value() default "";
	
	/** specify a class declaring this field */
	String clazz() default "";
}