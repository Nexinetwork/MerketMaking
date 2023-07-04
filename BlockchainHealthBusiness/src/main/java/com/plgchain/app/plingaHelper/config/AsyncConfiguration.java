/**
 *
 */
package com.plgchain.app.plingaHelper.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import lombok.Data;

/**
 * @author eae966
 *
 */
//@Data
//@EnableAsync
//@Configuration
//@ConfigurationProperties(prefix = "spring.async.thread.pool")
public class AsyncConfiguration implements AsyncConfigurer {

	private final static Logger logger = LoggerFactory.getLogger(AsyncConfiguration.class);

	private int coreSize;
	private int maxSize;
	private int queueCapacity;

	@Override
	public Executor getAsyncExecutor() {
		logger.info("spring.async.thread.pool.coreSize : " + coreSize);
		logger.info("spring.async.thread.pool.maxSize : " + maxSize);
		logger.info("spring.async.thread.pool.queueCapacity : " + queueCapacity);
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(coreSize);
		executor.setMaxPoolSize(maxSize);
		executor.setQueueCapacity(queueCapacity);
		executor.setThreadNamePrefix("worker-exec-");
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		executor.initialize();
		return executor;
	}

	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return (ex, method, params) -> {
			logger.error(ex.getMessage(), ex);
		};
	}

}
