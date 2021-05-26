package com.chargerlink.iot.mqtt.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * @author GISirFive
 * @date Created on 16:38 2019/5/31.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "iot.mqtt")
public class MqttConfig {

    private String host;

    private String clientId;

    /**
     * 连接用户名
     */
    private String username;

    /**
     * 连接密码
     */
    private String password;

    /**
     * 监听者默认的topic
     */
    private String defaultTopic;

    /**
     * 是否启用监听者
     */
    private boolean enableConsumer = false;

    private int defaultQos = 0;

    private int timeout = 30;

    private int keepAlive = 60;

    private int maxInflight = 10000;

    /**
     * 设置消息回调的bean
     */
    private String callbackBeanName;
}
