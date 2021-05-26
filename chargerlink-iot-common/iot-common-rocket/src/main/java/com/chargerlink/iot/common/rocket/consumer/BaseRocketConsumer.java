package com.chargerlink.iot.common.rocket.consumer;

import com.chargerlink.iot.common.consumer.IMessageListener;
import com.chargerlink.iot.common.entity.Message;
import com.chargerlink.iot.common.rocket.consumer.config.RocketConsumerProperties;
import com.chargerlink.iot.common.rocket.entity.RocketTopic;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.MessageListener;
import org.apache.rocketmq.client.exception.MQClientException;

import java.lang.management.ManagementFactory;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * RocketMQ的消费者抽象类
 *
 * @author GISirFive
 * @date Create on 2019/6/26 21:07
 */
public abstract class BaseRocketConsumer<T extends Message> {

    private static AtomicInteger PRODUCER_INDEX = new AtomicInteger();

    /**
     * 消息监听器，负责处理当前消费者收到的消息
     */
    protected IMessageListener<T> listener;
    /**
     * 消费者配置属性
     */
    protected RocketConsumerProperties properties;
    /**
     * 消费者topic
     */
    protected RocketTopic topic;
    /**
     * 消费者
     */
    private DefaultMQPushConsumer consumer;

    BaseRocketConsumer(IMessageListener listener, RocketConsumerProperties properties) {
        this.listener = listener;
        this.properties = properties;
        this.topic = (RocketTopic) listener.getTopic();
    }

    /**
     * 初始化消费者连接，启动消费
     *
     * @throws Exception
     */
    public void start() throws MQClientException {
        consumer = new DefaultMQPushConsumer();
        //broker地址
        consumer.setNamesrvAddr(properties.getNameServerAddress());
        //组名
        consumer.setConsumerGroup(topic.getGroupName());
        //VIP通道
        consumer.setVipChannelEnabled(properties.getVipChannelEnable());
        //实例名
        consumer.setInstanceName(getInstanceName("c"));
        //起始消费位置
        consumer.setConsumeFromWhere(properties.getConsumeFromWhere());
        //topic
        consumer.subscribe(topic.getValue(), topic.getTags());
        //设置消息监听
        consumer.registerMessageListener((MessageListener) this);
        consumer.start();
    }

    /**
     * 销毁消费者
     *
     * @throws Exception
     */
    public void destroy() throws Exception {
        if (consumer != null) {
            consumer.shutdown();
        }
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
