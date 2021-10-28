package com.Reflector;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
public @interface ReflectTest
{
	/** specify the score of the unit test */
	int value() default 1;
	
	/** description about the unit test */
	String desc() default "";
}