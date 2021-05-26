package com.chargerlink.iot.common.connector.entity;

import io.netty.channel.ChannelHandlerContext;
import lombok.Data;

/**
 * TCP会话封装实体
 *
 * @Author: ZH
 * @date Created on 14:54 2018/9/7.
 */
@Data
public class TCPSession {
    /**
     * 客户端ID
     */
    private String clientId;
    /**
     * 会话
     */
    private ChannelHandlerContext context;
    /**
     * 会话创建时间
     */
    private Long createTime;
    /**
     * 最后一次通讯时间
     */
    private Long lastCallTime;

}
