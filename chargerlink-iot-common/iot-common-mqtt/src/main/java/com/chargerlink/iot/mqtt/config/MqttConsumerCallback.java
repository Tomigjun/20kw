package com.chargerlink.iot.mqtt.config;

import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * @author GISirFive
 * @date Created on 11:08 2019/6/11.
 */
public interface MqttConsumerCallback {
    /**
     *  MQTT接收到消息的回调
     * @param topic
     * @param mqttMessage
     */
    void messageArrived(String topic, MqttMessage mqttMessage);
}
