package com.chargerlink.iot.common.rocket.consumer.config;

import com.chargerlink.iot.common.consumer.IMessageListener;
import com.chargerlink.iot.common.rocket.consumer.BaseRocketConsumer;
import com.chargerlink.iot.common.rocket.consumer.RocketConsumerConcurrently;
import com.chargerlink.iot.common.rocket.entity.RocketTopic;
import com.chargerlink.iot.common.topic.ITopic;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.exception.MQClientException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * RocketMQ消费者自动配置
 *
 * @author GISirFive
 * @date Create on 2020/3/1 22:51
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(RocketConsumerProperties.class)
@ConditionalOnProperty(name = RocketConsumerProperties.FLAG_PREFIX + ".enable", havingValue = "true")
public class RocketConsumerAutoConfiguration implements InitializingBean {

    private static final String ROCKET_CONSUMER_TASK_EXECUTOR = "rocketConsumerTaskExecutor";

    @Autowired
    private ApplicationContext context;

    private RocketConsumerProperties properties;
    /**
     * 消费者列表
     */
    private List<BaseRocketConsumer> consumerList = new ArrayList<>();

    public RocketConsumerAutoConfiguration(RocketConsumerProperties properties) {
        this.properties = properties;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 消费者地址
        if (StringUtils.isBlank(properties.getNameServerAddress())) {
            log.error("RocketConsumer初始化失败", new NullPointerException("消费者地址[rocket.consumer.name-server-address]不能为空"));
            SpringApplication.exit(context);
        }
        List<RocketTopic> topicList = (List<RocketTopic>) properties.getCompleteTopics();
        if (CollectionUtils.isEmpty(topicList)) {
            return;
        }
        for (int i = 0; i < topicList.size(); i++) {
            RocketTopic topic = topicList.get(i);
            if (StringUtils.isBlank(topic.getGroupName())) {
                log.error("RocketProducer初始化失败", new NullPointerException(String.format("消费者组[rocket.consumer.topics[%s].group-name]不能为空", i)));
                SpringApplication.exit(context);
            }
        }
        //初始化consumer
        Map<String, IMessageListener> listenerMap = context.getBeansOfType(IMessageListener.class, true, true);
        for (Map.Entry<String, IMessageListener> entry : listenerMap.entrySet()) {
            //NOTE 校验topic，通过getTopic()获取的topic可能是用户new出来的，而不是从yml中定义的
            ITopic topic = entry.getValue().getTopic();
            if (topic == null) {
                log.error("RocketConsumer初始化失败", new IllegalArgumentException(String.format("class[%s]中getTopic()方法返回为空", entry.getKey())));
                continue;
            }
            //只处理Rocket支持的Topic
            if (!(topic instanceof RocketTopic)) {
                log.error("RocketConsumer初始化失败", new IllegalArgumentException(String.format("class[%s]中getTopic()返回了错误的类型%s，应该返回%s",
                        entry.getKey(), topic.getClass(), RocketTopic.class)));
                continue;
            }
            if (StringUtils.isBlank(topic.getId())) {
                log.error("RocketConsumer初始化失败", new NullPointerException(String.format("class[%s]中，topic[%s]的id为空",
                        topic.getClass().getName(), topic.toString())));
                SpringApplication.exit(context);
            }
            if (StringUtils.isBlank(topic.getValue())) {
                log.error("RocketConsumer初始化失败", new NullPointerException(String.format("class[%s]中，topic[%s]的value为空",
                        topic.getClass().getName(), topic.toString())));
                SpringApplication.exit(context);
            }
            if (StringUtils.isBlank(((RocketTopic) topic).getGroupName())) {
                log.error("RocketConsumer初始化失败", new NullPointerException(String.format("class[%s]中，topic[%s]的groupName为空",
                        topic.getClass().getName(), topic.toString())));
                SpringApplication.exit(context);
            }

            BaseRocketConsumer consumer = new RocketConsumerConcurrently<>(entry.getValue(), properties);
            consumerList.add(consumer);
        }

        for (BaseRocketConsumer consumer : consumerList) {
            try {
                consumer.start();
            } catch (MQClientException e) {
                log.error(String.format("Rocket消费者初始化失败[consumer=%s]", consumer.toString()), e);
                SpringApplication.exit(context);
            }
        }
        log.info("RocketConsumer初始化完成");
    }

    /**
     * 销毁consumer
     */
    @PreDestroy
    public void destroy() {
        for (BaseRocketConsumer consumer : consumerList) {
            try {
                consumer.destroy();
            } catch (Exception e) {
                log.error("Rocket消费者销毁失败", e);
            }
        }
    }

    /**
     * 消费者专用线程池
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public Executor consumerTaskExecutor() {
        ThreadPoolTaskExecutor logTaskExecutor = new ThreadPoolTaskExecutor();
        logTaskExecutor.setThreadNamePrefix(ROCKET_CONSUMER_TASK_EXECUTOR + "-");
        logTaskExecutor.setCorePoolSize(Runtime.getRuntime().availableProcessors() * 2 + 1);
        logTaskExecutor.setMaxPoolSize(100);
        logTaskExecutor.setKeepAliveSeconds(10);
        //缓存队列大小
        logTaskExecutor.setQueueCapacity(1000);
        logTaskExecutor.setRejectedExecutionHandler((Runnable r, ThreadPoolExecutor executor) ->
                log.error("{}线程池队列已满[poolSize={}]", ROCKET_CONSUMER_TASK_EXECUTOR, executor.getPoolSize()));
        logTaskExecutor.initialize();
        return logTaskExecutor;
    }

}