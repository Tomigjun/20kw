package com.chargerlink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author GISirFive
 * @SpringBootApplication 注解等价于以默认属性使用 @Configuration + @EnableAutoConfiguration + @ComponentScan
 * @since 2018年9月7日18:19:45
 */
@EnableScheduling
@EnableAsync
@SpringBootApplication
public class MyApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(MyApplication.class, args);
    }

//    @Bean(name = "threadPoolTaskExecutor")
//    public Executor threadPoolTaskExecutor() {
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        executor.setCorePoolSize(8);
//        executor.setMaxPoolSize(1000);
//        return executor;
//    }

}
