package com.Reflector;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
public @interface ReflectMethod
{
	/** specify a name of the method */
	String value() default "";
	
	/** specify a class containing this method */
	String clazz() default "";
	
	/** specify a parameter signature of this method */
	Class<String>[] params();
}