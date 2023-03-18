package com.gy.chatbot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class ThreadPoolConfig {

	@Bean
	public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
		ThreadPoolTaskExecutor threadPool = new ThreadPoolTaskExecutor();
		threadPool.setCorePoolSize(5);
		threadPool.setQueueCapacity(Integer.MAX_VALUE);
		threadPool.setWaitForTasksToCompleteOnShutdown(true);
		threadPool.setAwaitTerminationSeconds(60);
		threadPool.setThreadNamePrefix("WxThreadPool");
		threadPool.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
		threadPool.initialize();
		return threadPool;
	}
}
