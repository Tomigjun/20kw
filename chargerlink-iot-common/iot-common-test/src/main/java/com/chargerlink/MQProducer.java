//package com.chargerlink;
//
//import com.alibaba.fastjson.JSON;
//import com.chargerlink.iot.common.entity.MessageBuilder;
//import com.chargerlink.iot.common.producer.IMessageProducer;
//import com.chargerlink.iot.common.topic.ITopic;
//import com.chargerlink.iot.common.topic.TopicManager;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
///**
// * @author GISirFive
// * @date Create on 2019/11/21 11:03
// */
//@Slf4j
//@Component
//public class MQProducer {
//
//    @Autowired
//    private IMessageProducer messageProducer;
//
//    @Autowired
//    private TopicManager topicManager;
//
//    @Scheduled(cron = "0/5 * * * * ?")
//    public void task1() {
//        ITopic topic = topicManager.getTopicById("100");
//        MessageBuilder builder = new MessageBuilder<byte[]>().protocolId("11111").traceId("22222").body("设备发过来的消息".getBytes());
//        log.info("发送消息给Gateway[message={}, topic={}]", JSON.toJSONString(builder), JSON.toJSONString(topic));
//        messageProducer.send(topic, builder);
//    }
//
//}
