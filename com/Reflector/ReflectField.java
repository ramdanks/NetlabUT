package com.Reflector;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
public @interface ReflectField
{
	/** specify a field name */
	String value() default "";
	
	/** specify a class declaring this field */
	String clazz() default "";
}