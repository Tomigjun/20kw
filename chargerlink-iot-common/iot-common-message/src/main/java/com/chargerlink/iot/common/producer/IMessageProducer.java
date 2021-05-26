package com.chargerlink.iot.common.producer;

import com.chargerlink.common.exception.ChargerlinkException;
import com.chargerlink.iot.common.entity.Message;
import com.chargerlink.iot.common.entity.MessageBuilder;
import com.chargerlink.iot.common.topic.ITopic;

/**
 * 消息生产者
 *
 * @author GISirFive
 * @date Create on 2020/2/26 16:42
 */
public interface IMessageProducer {

    /**
     * 发送消息
     *
     * @param topic
     * @param builder
     * @return 发送出去的消息
     * @throws ChargerlinkException
     */
    <T> Message<T> send(ITopic topic, MessageBuilder<T> builder) throws ChargerlinkException;


    /**
     * 发送消息
     *
     * @param topic
     * @param message
     * @return 发送出去的消息
     * @throws ChargerlinkException
     */
    <T> Message<T> send(ITopic topic, Message<T> message) throws ChargerlinkException;

    /**
     * 发送消息并异步接收回调
     *
     * @param topic
     * @param builder
     * @param callback
     * @throws ChargerlinkException
     */
    @Deprecated
    default void send(ITopic topic, MessageBuilder builder, Object callback) throws ChargerlinkException {

    }

}
