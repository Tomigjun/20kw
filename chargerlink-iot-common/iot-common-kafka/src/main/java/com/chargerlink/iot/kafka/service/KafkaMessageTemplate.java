//package com.chargerlink.iot.kafka.service;
//
//import com.chargerlink.iot.api.MessageTemplate;
//import com.chargerlink.iot.common.entity.base.MetaMessage;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Service;
//
//import lombok.extern.slf4j.Slf4j;
//
///**
// * kafka消息生产者实现
// *
// * @author GISirFive
// * @date Created on 16:05 2019/5/30.
// */
//@Slf4j
//@Service
//public class KafkaMessageTemplate implements MessageTemplate {
//
//    @Autowired
//    private KafkaTemplate<String, byte[]> kafkaTemplate;
//
//    /**
//     * 发布消息
//     *
//     * @param metaMessage
//     */
//    @Override
//    public void publish(MetaMessage metaMessage) {
//        kafkaTemplate.send(metaMessage.getTopic(), metaMessage.getMessageBody());
//    }
//}
