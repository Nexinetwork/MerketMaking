package com.plgchain.app.plingaHelper.annotation;

import java.lang.annotation.*;

@Target({ElementType.FIELD,ElementType.TYPE})
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Excel {

    String name() ;

}
