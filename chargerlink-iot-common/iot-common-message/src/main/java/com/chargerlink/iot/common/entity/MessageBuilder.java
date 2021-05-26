package com.chargerlink.iot.common.entity;

import com.chargerlink.common.util.AssertUtils;
import com.chargerlink.iot.common.topic.ITopic;
import com.chargerlink.iot.common.util.CodeGenerator;
import lombok.Getter;

/**
 * 基础消息构造器
 *
 * @author GISirFive
 * @date Create on 2019/6/20 16:01
 */
@Getter
public final class MessageBuilder<T> {
    /**
     * 消息ID<br>
     * 消息ID用来标记唯一一条消息
     */
    private String messageId;
    /**
     * 当前消息产生的时间戳
     */
    private long createTime;
    /**
     * 消息体
     */
    private T body;
    /**
     * 消息所属的链路ID<br>
     * 链路ID用来标记一条链路从产生到销毁的全过程<br>
     * 一条链路下会包含多条消息，消息覆盖范围从消息进入IOT平台到消息出IOT平台
     */
    private String traceId;
    /**
     * 设备消息协议ID
     */
    private String protocolId;
    /**
     * 消息发送方携带的topic信息
     */
    private ITopic topic;

    public MessageBuilder() {
        this.messageId = CodeGenerator.generateMessageId();
        this.createTime = System.currentTimeMillis();
    }

    /**
     * 复制参考消息中的{deviceId, traceId, protocolId}
     *
     * @param message 参考消息
     */
    public MessageBuilder(Message message) {
        this();
        this.traceId = message.getTraceId();
        this.protocolId = message.getProtocolId();
    }

    public Message<T> build() {
        //FIXME 在此处做参数校验不太合理
        AssertUtils.throwBlank(this.traceId, "链路ID不能为空");
        AssertUtils.throwBlank(this.protocolId, "协议ID不能为空");

        return new Message<>(this);
    }

    /**
     * 消息体
     */
    public MessageBuilder<T> body(T body) {
        this.body = body;
        return this;
    }

    /**
     * 获取消息所属的链路ID<br>
     * 链路ID用来标记一条链路从产生到销毁的全过程<br>
     * 一条链路下会包含多条消息，消息覆盖范围从消息进入IOT平台到消息出IOT平台
     */
    public MessageBuilder<T> traceId(String traceId) {
        this.traceId = traceId;
        return this;
    }

    /**
     * 设备消息协议ID
     */
    public MessageBuilder<T> protocolId(String protocolId) {
        this.protocolId = protocolId;
        return this;
    }

    /**
     * 消息发送方携带的topic信息
     *
     * @param topic
     * @return
     */
    public MessageBuilder<T> topic(ITopic topic) {
        this.topic = topic;
        return this;
    }

}
