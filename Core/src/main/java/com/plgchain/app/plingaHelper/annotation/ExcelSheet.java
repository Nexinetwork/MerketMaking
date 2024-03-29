package com.plgchain.app.plingaHelper.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelSheet {

    String name() default "sheet";

    int size() default 65535 ;

}
