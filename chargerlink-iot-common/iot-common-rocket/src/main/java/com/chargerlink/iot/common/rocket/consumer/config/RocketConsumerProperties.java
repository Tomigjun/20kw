package com.chargerlink.iot.common.rocket.consumer.config;

import com.chargerlink.iot.common.rocket.entity.RocketTopic;
import com.chargerlink.iot.common.topic.ITopic;
import com.chargerlink.iot.common.topic.ITopicProperties;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.List;

/**
 * 消费者配置参数
 *
 * @author GISirFive
 * @date Create on 2019/5/24 18:00
 */
@Data
@ConfigurationProperties(prefix = RocketConsumerProperties.FLAG_PREFIX)
public class RocketConsumerProperties implements ITopicProperties {

    public static final String FLAG_PREFIX = "iot.rocket.consumer";

    /**
     * 消费者是否启动
     */
    private boolean enable = false;
    /**
     * nameServer服务地址
     */
    private String nameServerAddress;
    /**
     * 消费者默认组名称
     */
    private String defaultGroupName;
    /**
     * 是否走VIP通道<br>
     * **默认为false**
     */
    private Boolean vipChannelEnable = false;
    /**
     * 消息延迟达到的最长时间**单位-毫秒**<br>
     * **默认为-1，即不考虑延迟**<br>
     * 超过此延迟时间到达的消息将不被消费，直接丢弃<br>
     */
    private Long messageDelayMaxSeconds = -1L;
    /**
     * 起始消费位置<br>
     * **默认为ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET，即从最近的偏移量开始消费**
     */
    private ConsumeFromWhere consumeFromWhere = ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET;
    /**
     * 消费模式<br>
     * **concurrently-并发消费模式（默认）**<br>
     * **orderly-有序消费模式**<br>
     */
    @Deprecated
    private String consumeMode = "concurrently";

    /**
     * topic列表
     */
    @NestedConfigurationProperty
    private List<RocketTopic> topics;

    /**
     * 获取完整的topic列表，在ITopic中定义的字段要确保完整，且在系统运行过程中不会修改
     *
     * @return
     */
    @Override
    public List<? extends ITopic> getCompleteTopics() {
        topics.forEach(topic -> {
            if (StringUtils.isBlank(topic.getGroupName())) {
                topic.setGroupName(getDefaultGroupName());
            }
        });
        return topics;
    }
}
