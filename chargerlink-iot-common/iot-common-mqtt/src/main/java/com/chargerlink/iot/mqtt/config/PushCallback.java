package com.chargerlink.iot.mqtt.config;

import com.alibaba.fastjson.JSON;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author GISirFive
 * @date Created on 15:21 2019/5/22.
 */
@Slf4j
@Component
public class PushCallback implements MqttCallback {
    @Autowired
    private MqttConfig mqttConfig;
    @Autowired
    private ApplicationContext applicationContext;

    private MqttConsumerCallback mqttConsumerCallback;

    @Override
    public void connectionLost(Throwable throwable) {
        log.error("MQTT的连接丢失已丢失[throwable = {}]", throwable);
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        log.debug("MQTT接收到消息[topic={},mqttMessage = {}]", topic, JSON.toJSONString(mqttMessage));
        if (mqttConfig.isEnableConsumer()) {
            if (mqttConsumerCallback == null) {
                mqttConsumerCallback = (MqttConsumerCallback) applicationContext.getBean(mqttConfig.getCallbackBeanName());
            }
            mqttConsumerCallback.messageArrived(topic, mqttMessage);
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        try {
            log.debug("MQTT消息发送成功[message = {}]", JSON.toJSONString(iMqttDeliveryToken.getMessage()));
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
