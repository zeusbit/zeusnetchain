package org.fc.cs.api.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.felix.ipojo.annotations.Stereotype;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Stereotype
public @interface MessageEntity {
	/**
	 * 定义结构体的主键
	 * @return
	 */
	String key() default "";
	
	/**
	 * 定义结构体的标示
	 * @return
	 */
	String data() default "";
	
	
}
