package com.NetlabUT.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@ReflectKind
public @interface ReflectCtor
{
	/** specify a class name which contain this constructor */
	String owner();
	
	/** specify a parameter signature of this constructor */
	Class<?>[] params();
}