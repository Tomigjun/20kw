//package com.chargerlink;
//
//import com.alibaba.fastjson.JSON;
//import com.chargerlink.common.exception.ChargerlinkException;
//import com.chargerlink.iot.common.consumer.IMessageListener;
//import com.chargerlink.iot.common.entity.Message;
//import com.chargerlink.iot.common.topic.ITopic;
//import com.chargerlink.iot.common.topic.TopicManager;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Component;
//
///**
// * 测试消费
// *
// * @author GISirFive
// * @date Create on 2019/5/28 10:26
// */
//@Slf4j
//@Component
//public class DemoConsumer implements IMessageListener<Message<byte[]>> {
//
//    private int count = 0;
//
//    @Autowired
//    private TopicManager topicManager;
//
//    @Override
//    public ITopic getTopic() {
//        return topicManager.getTopicById("101");
//    }
//
//    /**
//     * 收到消息
//     *
//     * @param message
//     * @throws ChargerlinkException
//     */
//    @Async
//    @Override
//    public void onMessageReceived(Message<byte[]> message) throws ChargerlinkException {
//        log.info("收到Gateway发来的消息[count={}, message={}]", ++count, JSON.toJSONString(message));
//    }
//
//}
