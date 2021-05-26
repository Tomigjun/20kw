package com.chargerlink.iot.common.rocket.producer.config;

import com.chargerlink.iot.common.rocket.entity.RocketTopic;
import com.chargerlink.iot.common.topic.ITopic;
import com.chargerlink.iot.common.topic.ITopicProperties;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
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
@ConfigurationProperties(prefix = RocketProducerProperties.FLAG_PREFIX)
public class RocketProducerProperties implements ITopicProperties {

    public static final String FLAG_PREFIX = "iot.rocket.producer";

    /**
     * 生产者是否启动
     */
    private boolean enable = false;
    /**
     * nameServer服务地址
     */
    private String nameServerAddress;
    /**
     * 默认生产组名称**如果topic中设置了groupName，则该属性失效**
     */
    private String defaultGroupName;
    /**
     * 当前节点最多可容纳的生产者数量**默认为2个**
     */
    private Integer maxProducerCount = 2;
    /**
     * 是否走VIP通道<br>
     * **默认为false**
     */
    private Boolean vipChannelEnable = false;
    /**
     * 消息发送的超时时间**单位-毫秒** **默认为3秒**
     */
    private Integer sendMsgTimeout;
    /**
     * 消息发送失败后最大重试次数**默认为2次**
     */
    private Integer retryTimesWhenSendFailed;
    /**
     * 单条消息体超过最大字节后进行压缩**默认为4096字节**
     */
    private Integer compressMsgBodyOverHowmuch;
    /**
     * 单条消息体最大长度**默认为4M**
     */
    private Integer maxMessageSize;

    /**
     * topic列表
     */
    @NestedConfigurationProperty
    private List<RocketTopic> topics;


    /**
     * 获取topic列表
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
