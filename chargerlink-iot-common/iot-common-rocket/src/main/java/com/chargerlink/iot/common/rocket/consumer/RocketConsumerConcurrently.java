package com.chargerlink.iot.common.rocket.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.chargerlink.common.exception.ChargerlinkException;
import com.chargerlink.iot.common.constant.ConsumeErrorTypeEnum;
import com.chargerlink.iot.common.consumer.IMessageListener;
import com.chargerlink.iot.common.entity.Message;
import com.chargerlink.iot.common.rocket.consumer.config.RocketConsumerProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

/**
 * RocketMQ的消费者，负责接收指定topic中的数据，并传递给{@link com.chargerlink.iot.common.consumer.IMessageListener}处理
 *
 * @author GISirFive
 * @date Create on 2019/6/26 19:46
 */
@Slf4j
public class RocketConsumerConcurrently<T extends Message> extends BaseRocketConsumer<T> implements MessageListenerConcurrently {

    public RocketConsumerConcurrently(IMessageListener<T> listener, RocketConsumerProperties properties) {
        super(listener, properties);
    }

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        //默认每次只接收1条消息  consumer.setConsumeMessageBatchMaxSize()
        MessageExt msg = msgs.get(0);
        //校验消息是否过期
        if (properties.getMessageDelayMaxSeconds() != -1L) {
            long duration = System.currentTimeMillis() - msg.getBornTimestamp();
            if (duration > properties.getMessageDelayMaxSeconds()) {
                log.error("当前消息已过期[message={}]", msg.toString());
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        }
        try {
            T message = JSON.parseObject(new String(msg.getBody()), new TypeReference<T>() {
            });
            listener.onMessageReceived(message);
        } catch (ChargerlinkException ce) {
            ConsumeErrorTypeEnum errorType = ConsumeErrorTypeEnum.getEnumByErrorCode(ce.getErrorCode());
            //NOTE 根据错误码判断消息应该如何处理
            switch (errorType) {
                case INTERNAL_ERROR_RETRY:
                case BUSINESS_ERROR_RETRY:
                    //消息重试
                    log.error(String.format("消息处理过程抛错，稍后重试[message=%s]>>", msg.toString()), ce);
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                case INTERNAL_ERROR_DROP:
                case BUSINESS_ERROR_DROP:
                case UNKNOWN_ERROR:
                default:
                    //消息丢弃
                    log.error(String.format("消息处理过程抛错，丢弃消息[message=%s]>>", msg.toString()), ce);
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        } catch (Exception e) {
            //消息丢弃
            log.error(String.format("消息处理过程抛错，丢弃消息[message=%s]>>", msg.toString()), e);
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}
