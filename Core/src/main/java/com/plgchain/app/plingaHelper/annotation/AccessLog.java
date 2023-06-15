package com.plgchain.app.plingaHelper.annotation;


import java.lang.annotation.*;

import com.plgchain.app.plingaHelper.constant.AdminModule;

@Target({ElementType.METHOD})
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface AccessLog {
    String operation();
    AdminModule module();
}

