package com.chargerlink.iot.common.rocket.producer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.chargerlink.common.exception.ChargerlinkException;
import com.chargerlink.iot.common.entity.Message;
import com.chargerlink.iot.common.entity.MessageBuilder;
import com.chargerlink.iot.common.producer.IMessageProducer;
import com.chargerlink.iot.common.rocket.entity.RocketTopic;
import com.chargerlink.iot.common.rocket.producer.config.RocketProducerAutoConfiguration;
import com.chargerlink.iot.common.rocket.producer.config.RocketProducerProperties;
import com.chargerlink.iot.common.topic.ITopic;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * @author GISirFive
 * @date Create on 2020/2/28 10:25
 */
@Slf4j
@Component
@ConditionalOnProperty(name = RocketProducerProperties.FLAG_PREFIX + ".enable", havingValue = "true")
public class RocketProducer implements IMessageProducer {

    @Autowired
    private RocketProducerAutoConfiguration configuration;

    /**
     * 发送消息
     *
     * @param topic
     * @param builder
     * @throws ChargerlinkException
     */
    @Override
    public <T> Message<T> send(ITopic topic, MessageBuilder<T> builder) throws ChargerlinkException {
        if (!(topic instanceof RocketTopic)) {
            throw new ChargerlinkException(String.format("%s不是%s类或其子类，无法通过%s发送消息",
                    topic.getClass().getName(), RocketTopic.class.getName(), RocketProducer.class.getSimpleName()));
        }
        return send(topic, builder.build());
    }

    /**
     * 发送消息
     *
     * @param topic
     * @param message
     * @throws ChargerlinkException
     */
    @Override
    public <T> Message<T> send(ITopic topic, com.chargerlink.iot.common.entity.Message<T> message) throws ChargerlinkException {
        //在message中补全topic信息
        if (message.getTopic() == null) {
            message.setTopic(topic);
        }
        org.apache.rocketmq.common.message.Message msg = new org.apache.rocketmq.common.message.Message();
        msg.setTopic(topic.getValue());
        msg.setTags(topic.getValueFilter());
        msg.setBody(JSON.toJSONBytes(message, SerializerFeature.WriteClassName));
        // 设置每条消息的唯一key值，方便日后查找消息
        msg.setKeys(System.currentTimeMillis() + "");
        try {
            DefaultMQProducer producer = configuration.getMQProducer((RocketTopic) topic);
            log.debug("ROCKET MQ发送到消息队列[message={}]", JSON.toJSONString(msg));
            SendResult result = producer.send(msg);
            log.debug("ROCKET MQ消息发送结果[result={}]", JSON.toJSONString(result));
        } catch (MQClientException e) {
            log.error("MQ客户端异常>>>", e);
        } catch (RemotingException e) {
            log.error("MQ远程调用异常>>>", e);
        } catch (MQBrokerException e) {
            log.error("MQBroker异常>>>", e);
        } catch (Exception e) {
            log.error("MQInterruptedException异常>>>", e);
        }
        return message;
    }

}
