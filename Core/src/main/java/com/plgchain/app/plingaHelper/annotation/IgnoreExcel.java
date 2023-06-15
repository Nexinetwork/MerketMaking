package com.plgchain.app.plingaHelper.annotation;

import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface IgnoreExcel {

    boolean value() default true ;
}
