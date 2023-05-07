package zerobase.dividend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
public class SchedulerConfig implements SchedulingConfigurer{
	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistar) {
		ThreadPoolTaskScheduler threadPool = new ThreadPoolTaskScheduler();
		
		int n = Runtime.getRuntime().availableProcessors();
		threadPool.setPoolSize(n);
		threadPool.initialize();
		
		taskRegistar.setTaskScheduler(threadPool);
	}
}
