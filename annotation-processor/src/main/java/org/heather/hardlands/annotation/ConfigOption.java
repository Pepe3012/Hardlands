package org.heather.hardlands.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({})
@Retention(RetentionPolicy.SOURCE)
public @interface ConfigOption {

    String name();

    Class<?> type();

    String key() default "";
}