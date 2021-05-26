package com.chargerlink.iot.mqtt.config;

import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author GISirFive
 * @date Created on 16:45 2019/5/31.
 */
@Slf4j
@Component
@EnableConfigurationProperties(MqttConfig.class)
public class MqttClientInitializer {

    @Autowired
    private MqttConfig mqttConfig;
    @Autowired
    private MqttCallback pushCallback;
    @Autowired
    private ConnectCallback connectCallback;

    @Bean("mqttAsyncClient")
    @ConditionalOnBean(name = "mqttConfig")
    public MqttAsyncClient createMqttClient() {
        try {
            MqttAsyncClient mqttClient = new MqttAsyncClient(mqttConfig.getHost(), mqttConfig.getClientId(), new MemoryPersistence());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setUserName(mqttConfig.getUsername());
            options.setPassword(mqttConfig.getPassword().toCharArray());
            options.setConnectionTimeout(mqttConfig.getTimeout());
            options.setKeepAliveInterval(mqttConfig.getKeepAlive());
            options.setMaxInflight(mqttConfig.getMaxInflight());
            mqttClient.setCallback(pushCallback);
            IMqttToken mqttToken = mqttClient.connect(options, this, connectCallback);
            mqttToken.waitForCompletion();
            return mqttClient;
        } catch (MqttException e) {
            e.printStackTrace();
            log.error("MqttAsyncClient组件初始化失败[exception = {}]", e);
        }
        return null;
    }
}
