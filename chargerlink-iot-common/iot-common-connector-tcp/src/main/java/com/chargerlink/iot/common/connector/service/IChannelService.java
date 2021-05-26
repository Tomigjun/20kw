package com.chargerlink.iot.common.connector.service;

import com.chargerlink.common.exception.ChargerlinkException;
import io.netty.channel.ChannelHandlerContext;

/**
 * Channel管理
 *
 * @author GISirFive
 * @date Create on 2020/3/12 15:15
 */
public interface IChannelService {

    /**
     * 新增tcp会话
     *
     * @param context
     * @throws ChargerlinkException
     */
    void createChannel(ChannelHandlerContext context) throws ChargerlinkException;

    /**
     * 关闭tcp会话
     *
     * @param context
     * @throws ChargerlinkException
     */
    void closeChannel(ChannelHandlerContext context) throws ChargerlinkException;

    /**
     * 关闭TCP会话
     *
     * @param channelId
     * @throws ChargerlinkException
     */
    void closeChannelById(String channelId) throws ChargerlinkException;

    /**
     * 读取消息
     *
     * @param channelId
     * @param message
     * @throws ChargerlinkException
     */
    void readMessage(String channelId, Object message) throws ChargerlinkException;

    /**
     * 写入消息
     *
     * @param channelId
     * @param message
     * @throws ChargerlinkException
     */
    void writeMessage(String channelId, byte[] message) throws ChargerlinkException;

    /**
     * 获取活跃的channel数量
     *
     * @return
     */
    Integer getActivateChannelCount();

}
