package com.Reflector;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.FIELD})
public @interface ReflectTester
{
	/** specify class name that is tested using this class */
	String value();
}