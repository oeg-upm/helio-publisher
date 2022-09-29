package semanticgateway;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableAsync
@Configuration
@EnableScheduling
public class AsyncConfig implements AsyncConfigurer {


 	@Bean()
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(150);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("Helio-");
        executor.initialize();
        return executor;
    }



 	@Bean
 	  public AsyncTaskExecutor taskExecutor() {
 	    return new ConcurrentTaskExecutor(Executors.newCachedThreadPool());
 	  }
}
