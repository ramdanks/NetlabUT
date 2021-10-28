package com.Reflector;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
public @interface ReflectCtor
{
	/** specify a class name which contain this constructor */
	String value() default "";
	
	/** specify a parameter signature of this constructor */
	Class<?>[] params() default {};
}