package com.NetlabUT.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@ReflectKind
public @interface ReflectMethod
{
	/** specify a name of the method */
	String value() default "";
	
	/** specify a class containing this method */
	String owner();
	
	/** specify a parameter signature of this method */
	Class<?>[] params();
}