package com.chargerlink.iot.common.consumer;

import com.chargerlink.common.exception.ChargerlinkException;
import com.chargerlink.iot.common.entity.Message;
import com.chargerlink.iot.common.topic.ITopic;

/**
 * 消息监听器
 *
 * @author GISirFive
 * @date Create on 2019/6/20 14:37
 */
public interface IMessageListener<T extends Message> {

    /**
     * 当前listener需要监听的topic<br>
     * 满足以下条件中的一条，即可通过{@link com.chargerlink.iot.common.topic.TopicManager}中的getTopicById()获取到topic:<br>
     * 1-topic是在yml中配置的<br>
     * 2-topic已经作为一个bean交给SpringContext管理<br>
     *
     * @return
     */
    ITopic getTopic();

    /**
     * 收到消息
     *
     * @param message
     * @throws ChargerlinkException
     */
    void onMessageReceived(T message) throws ChargerlinkException;

}
