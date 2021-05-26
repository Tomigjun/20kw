package com.chargerlink.iot.common.connector.service;

import com.chargerlink.common.exception.ChargerlinkException;

/**
 * TCP消息接收
 *
 * @author GISirFive
 * @date Create on 2019/12/5 10:56
 */
public interface ITCPMessageListener {

    /**
     * 收到消息
     *
     * @param message       消息体
     * @param protocolId 协议ID
     * @param channelId  通道ID
     * @throws ChargerlinkException
     */
    void onMessageReceived(byte[] message, String protocolId, String channelId) throws ChargerlinkException;
}
