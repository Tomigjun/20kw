//package com.chargerlink.iot.mqtt.service;
//
//import com.alibaba.fastjson.JSON;
//import com.chargerlink.iot.api.MessageTemplate;
//import com.chargerlink.iot.common.entity.base.MetaMessage;
//
//import org.eclipse.paho.client.mqttv3.IMqttToken;
//import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
//import org.eclipse.paho.client.mqttv3.MqttException;
//import org.eclipse.paho.client.mqttv3.MqttMessage;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import lombok.extern.slf4j.Slf4j;
//
///**
// * MQTT消息封装，这个类就是一个统一封装，并不能发挥各种中间件的特性
// *
// * @author GISirFive
// * @date Created on 14:26 2019/6/3.
// */
//@Slf4j
//@Service
//public class MqttMessageTemplate implements MessageTemplate {
//
//    @Autowired
//    private MqttAsyncClient mqttAsyncClient;
//
//    /**
//     * MQTT发布消息
//     *
//     * @param metaMessage
//     */
//    @Override
//    public void publish(MetaMessage metaMessage) {
//        MqttMessage message = new MqttMessage();
//        int qos = metaMessage.getExtra().get("qos") != null ? (int) metaMessage.getExtra().get("qos") : 0;
//        boolean retained = metaMessage.getExtra().get("retained") != null && (boolean) metaMessage.getExtra().get("retained");
//        message.setQos(qos);
//        message.setRetained(retained);
//        message.setPayload(metaMessage.getMessageBody());
//        try {
//            mqttAsyncClient.publish(metaMessage.getTopic(), message);
//        } catch (MqttException e) {
//            e.printStackTrace();
//            log.error("MQTT消息发送失败[metaMessage = {},MqttException = {}]", JSON.toJSONString(metaMessage), e);
//        }
//    }
//
//    /**
//     * MQTT 新增订阅
//     *
//     * @param topic
//     * @param qos
//     * @return
//     */
//    public boolean subscribe(String topic, int qos) {
//        if (mqttAsyncClient.isConnected()) {
//            try {
//                IMqttToken mqttToken = mqttAsyncClient.subscribe(topic, qos);
//                log.info("MQTT 新增订阅成功[topic = {},qos = {}]", topic, qos);
//                return true;
//            } catch (MqttException e) {
//                log.info("MQTT 新增订阅失败[topic = {},qos = {}]", topic, qos);
//                e.printStackTrace();
//            }
//        }
//        log.info("MQTT 新增订阅失败，mqttAsyncClient尚未连接[topic = {},qos = {}]", topic, qos);
//        return false;
//    }
//
//    /**
//     * MQTT 取消订阅
//     *
//     * @param topic
//     */
//    public void unsubscribe(String topic) {
//        if (mqttAsyncClient.isConnected()) {
//            try {
//                mqttAsyncClient.unsubscribe(topic);
//            } catch (MqttException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//}
