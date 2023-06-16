package com.plgchain.app.plingaHelper.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.plgchain.app.plingaHelper.constant.LogAction;
import com.plgchain.app.plingaHelper.constant.LogType;
import com.plgchain.app.plingaHelper.entity.Log;
import com.plgchain.app.plingaHelper.service.LogService;

@Aspect
@Component
public class Logger {

	/*
	 * @Autowired private LogService logService;
	 */



    @Pointcut("@within(com.plgchain.app.plingaHelper.annotation.LogClass)")
    public void logClass() {}

    @Pointcut("@annotation(com.plgchain.app.plingaHelper.annotation.LogMethod)")
    public void logMethod() {}

    @Pointcut("@annotation(com.plgchain.app.plingaHelper.annotation.NotLogMethod)")
    public void notLogMethod() {}



    @Before("(logClass() ||  logMethod()) && !notLogMethod()")
    public void adviceBefore(JoinPoint jp)  {
        System.out.println("@Before called");
    }

    @After("(logClass() ||  logMethod()) && !notLogMethod()")
    public void adviceAfter(JoinPoint jp)  {
        System.out.println("Object created :");

    }

    @AfterThrowing("(logClass() ||  logMethod()) && !notLogMethod()")
    public void adviceAfterThrowing(JoinPoint jp){
        System.out.println("@AfterThrowing called");
    }

}