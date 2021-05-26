package com.chargerlink.iot.common.rocket.producer.config;

import com.chargerlink.common.exception.ChargerlinkException;
import com.chargerlink.iot.common.rocket.entity.RocketTopic;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * RocketMQ生产者自动配置
 *
 * @author GISirFive
 * @date Create on 2020/3/1 22:51
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(RocketProducerProperties.class)
@ConditionalOnProperty(name = RocketProducerProperties.FLAG_PREFIX + ".enable", havingValue = "true")
public class RocketProducerAutoConfiguration implements InitializingBean {

    private static AtomicInteger PRODUCER_INDEX = new AtomicInteger();

    @Autowired
    private ApplicationContext context;

    private RocketProducerProperties properties;

    /**
     * 生产者集合**key-groupName, value-{@link DefaultMQProducer}**
     */
    private Map<String, DefaultMQProducer> rocketMQProducerMap;

    public RocketProducerAutoConfiguration(RocketProducerProperties properties) {
        this.properties = properties;
        rocketMQProducerMap = new ConcurrentHashMap<>();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 生产者地址
        if (StringUtils.isBlank(properties.getNameServerAddress())) {
            log.error("RocketProducer初始化失败", new NullPointerException("生产者地址[rocket.producer.name-server-address]不能为空"));
            SpringApplication.exit(context);
        }
        List<RocketTopic> topicList = (List<RocketTopic>) properties.getCompleteTopics();
        if (CollectionUtils.isEmpty(topicList)) {
            return;
        }
        for (int i = 0; i < topicList.size(); i++) {
            RocketTopic topic = topicList.get(i);
            if (StringUtils.isBlank(topic.getGroupName())) {
                log.error("RocketProducer初始化失败", new NullPointerException(String.format("生产者组[rocket.producer.topics[%s].group-name]不能为空", i)));
                SpringApplication.exit(context);
            }
        }
    }

    /**
     * @param topic
     * @return
     */
    public DefaultMQProducer getMQProducer(RocketTopic topic) throws MQClientException, ChargerlinkException {
        //获取groupName
        String groupName = topic.getGroupName();
        if (StringUtils.isBlank(groupName)) {
            groupName = properties.getDefaultGroupName();
        }
        if (rocketMQProducerMap.containsKey(groupName)) {
            return rocketMQProducerMap.get(groupName);
        }
        //创建producer
        if (rocketMQProducerMap.size() == properties.getMaxProducerCount()) {
            throw new ChargerlinkException(String.format("生产者数量已达到当前节点设置的可容纳上限[%s=%s]",
                    "rocket.producer.max-producer-count", properties.getMaxProducerCount()));
        }
        DefaultMQProducer producer;
        synchronized (this) {
            producer = createMQProducer(properties, groupName);
            rocketMQProducerMap.put(groupName, producer);
        }
        return producer;
    }

    /**
     * 移除某个生产者
     *
     * @param topic
     */
    public void removeMQProducer(RocketTopic topic) {
        if (!rocketMQProducerMap.containsKey(topic.getGroupName())) {
            return;
        }
        DefaultMQProducer producer = rocketMQProducerMap.get(topic.getGroupName());
        try {
            producer.shutdown();
        } finally {
            rocketMQProducerMap.remove(topic.getGroupName());
        }
    }

    /**
     * 销毁consumer
     */
    @PreDestroy
    public void destroy() {
        for (DefaultMQProducer producer : rocketMQProducerMap.values()) {
            producer.shutdown();
        }
    }

    /**
     * 创建MQ生产者
     *
     * @param properties
     * @param groupName
     * @return
     * @throws Exception
     */
    private DefaultMQProducer createMQProducer(RocketProducerProperties properties, String groupName) throws MQClientException {
        DefaultMQProducer producer = new DefaultMQProducer();
        //生产者地址
        producer.setNamesrvAddr(properties.getNameServerAddress());
        // Producer组名
        producer.setProducerGroup(groupName);
        // 是否走VIP通道
        producer.setVipChannelEnabled(properties.getVipChannelEnable());
        // 实例名称
        producer.setInstanceName(getInstanceName("p"));
        // 消息发送超时时间
        if (properties.getSendMsgTimeout() != null) {
            producer.setSendMsgTimeout(properties.getSendMsgTimeout());
        }
        // 消息发送失败后重试次数
        if (properties.getRetryTimesWhenSendFailed() != null) {
            producer.setRetryTimesWhenSendFailed(properties.getRetryTimesWhenSendFailed());
        }
        // 单条消息体超过最大字节后进行压缩**默认为4096字节**
        if (properties.getCompressMsgBodyOverHowmuch() != null) {
            producer.setCompressMsgBodyOverHowmuch(properties.getCompressMsgBodyOverHowmuch());
        }
        // 单条消息体最大长度**默认为4M**
        if (properties.getMaxMessageSize() != null) {
            producer.setMaxMessageSize(properties.getMaxMessageSize());
        }
        // 本机IP
        // sender.setClientIP();
        // 通信层异步回调线程数
        // sender.setClientCallbackExecutorThreads(4);
        // 轮询Name Server时间间隔，单位毫秒
        // sender.setPollNameServerInteval(30000);
        // 向Broker发送心跳间隔时间，单位毫秒
        // sender.setHeartbeatBrokerInterval(30000);
        // 持久化Consumer消费进度间隔时间，单位毫秒
        // sender.setPersistConsumerOffsetInterval(5000);
        producer.start();
        log.info("RocketMQ生产者初始化成功[InstanceName = {}]", producer.getInstanceName());
        return producer;
    }

    /**
     * 获取实例唯一ID
     *
     * @param prefix 前缀
     * @return
     */
    private String getInstanceName(String prefix) {
        String info = ManagementFactory.getRuntimeMXBean().getName();
        int pid = (new Random()).nextInt();
        int index = info.indexOf("@");
        if (index > 0) {
            pid = Integer.parseInt(info.substring(0, index));
        }
        return String.format("%s-pid[%s]-index[%s]", prefix, pid, PRODUCER_INDEX.incrementAndGet());
    }
}
