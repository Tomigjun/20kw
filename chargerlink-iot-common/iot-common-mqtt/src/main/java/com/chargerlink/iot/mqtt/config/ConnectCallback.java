package com.chargerlink.iot.mqtt.config;

import com.alibaba.fastjson.JSON;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * MQTT连接成功回调方法
 *
 * @author GISirFive
 * @date Created on 14:10 2019/6/3.
 */
@Slf4j
@Component
@EnableConfigurationProperties(MqttConfig.class)
public class ConnectCallback implements IMqttActionListener {

    @Autowired
    private MqttConfig mqttConfig;
    @Autowired
    private ApplicationContext applicationContext;

    /**
     * This method is invoked when an action has completed successfully.
     *
     * @param asyncActionToken associated with the action that has completed
     */
    @Override
    public void onSuccess(IMqttToken asyncActionToken) {
        log.info("MQTT连接成功[ServerURI = {}]", asyncActionToken.getClient().getServerURI());
        try {
            if (mqttConfig.isEnableConsumer() && !StringUtils.isEmpty(mqttConfig.getDefaultTopic())) {

                IMqttToken mqttToken = asyncActionToken.getClient().subscribe(mqttConfig.getDefaultTopic(), mqttConfig.getDefaultQos());
                mqttToken.waitForCompletion();
                log.info("MQTT建立默认监听[DefaultTopic = {} , DefaultQos = {}]", mqttConfig.getDefaultTopic(), mqttConfig.getDefaultQos());
            }
        } catch (MqttException e) {
            e.printStackTrace();
            log.error("MQTT建立默认监听失败[exception = {}]", e);
        }
    }

    /**
     * This method is invoked when an action fails.
     * If a client is disconnected while an action is in progress
     * onFailure will be called. For connections
     * that use cleanSession set to false, any QoS 1 and 2 messages that
     * are in the process of being delivered will be delivered to the requested
     * quality of service next time the client connects.
     *
     * @param asyncActionToken associated with the action that has failed
     * @param exception        thrown by the action that has failed
     */
    @Override
    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
        log.error("mqtt连接失败[asyncActionToken = {},exception = {}]", JSON.toJSONString(asyncActionToken), exception);
    }
}
