package com.chargerlink.iot.common.entity;

import com.chargerlink.common.util.AssertUtils;
import com.chargerlink.iot.common.topic.ITopic;
import com.chargerlink.iot.common.util.CodeGenerator;
import lombok.Data;

/**
 * 基础消息信息接口
 *
 * @author GISirFive
 * @date Create on 2019/6/20 16:01
 */
@Data
public class Message<T> {

    /**
     * 消息所属的链路ID<br>
     * 链路ID用来标记一条链路从产生到销毁的全过程<br>
     * 一条链路下会包含多条消息，消息覆盖范围从消息进入IOT平台到消息出IOT平台
     */
    private String traceId;
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
     * 设备消息协议ID
     */
    private String protocolId;
    /**
     * 消息发送方携带的topic信息
     */
    private ITopic topic;

    protected Message() {
    }

    public Message(String protocolId, T body) {
        AssertUtils.throwBlank(protocolId, "协议ID不能为空");
        this.protocolId = protocolId;
        this.body = body;
        this.traceId = CodeGenerator.generateTraceId();
        this.messageId = CodeGenerator.generateMessageId();
        this.createTime = System.currentTimeMillis();
    }

    Message(MessageBuilder<T> builder) {
        this.messageId = builder.getMessageId();
        this.createTime = builder.getCreateTime();
        this.body = builder.getBody();
        this.traceId = builder.getTraceId();
        this.protocolId = builder.getProtocolId();
        this.topic = builder.getTopic();
    }

}
