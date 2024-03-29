package com.plgchain.app.plingaHelper.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.plgchain.app.plingaHelper.constant.LogAction;
import com.plgchain.app.plingaHelper.constant.LogType;
import com.plgchain.app.plingaHelper.entity.Log;
import com.plgchain.app.plingaHelper.microService.LogMicroService;

@Aspect
@Component
public class Logger {

	@Autowired
	private LogMicroService logMicroService;

	@Pointcut("@within(com.plgchain.app.plingaHelper.annotation.LogClass)")
	public void logClass() {
	}

	@Pointcut("@annotation(com.plgchain.app.plingaHelper.annotation.LogMethod)")
	public void logMethod() {
	}

	@Pointcut("@annotation(com.plgchain.app.plingaHelper.annotation.NotLogMethod)")
	public void notLogMethod() {
	}

	@Before("(logClass() ||  logMethod()) && !notLogMethod()")
	public void adviceBefore(JoinPoint jp) {
		System.out.println("@Before called");
	}

	@AfterReturning(pointcut = "(logClass() ||  logMethod()) && !notLogMethod()", returning = "result")
	public void adviceAfterReturning(JoinPoint jp, Object result) {
		System.out.println("Object created :" + result.toString());

		Log log = new Log();
		log.setLogAction(LogAction.SYSTEMACTION);
		log.setLogType(LogType.SYSTEMACTION);
		String res = String.format("Method %s has ben run,", jp.getSignature().getName());
		if (result != null)
			res += " and result is " + result.toString();
		log.setLogDetail(res);
		logMicroService.save(log);
	}

	@AfterThrowing("(logClass() ||  logMethod()) && !notLogMethod()")
	public void adviceAfterThrowing(JoinPoint jp) {
		System.out.println("@AfterThrowing called");
	}

}