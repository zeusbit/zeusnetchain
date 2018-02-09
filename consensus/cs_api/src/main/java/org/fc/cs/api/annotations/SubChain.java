package org.fc.cs.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SubChain {
	public String name() default "";

	public String chainAlias() default "";

	public Class keyClass() default void.class;

}
