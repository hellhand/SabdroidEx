package com.sabdroidex.utils.json;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
public @interface JSONGetter {
    
    String name() default "";
}
