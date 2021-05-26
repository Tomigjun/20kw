package com.chargerlink.iot.common.connector.netty.handler;

import com.chargerlink.iot.common.connector.config.TCPConnectorProperties;
import com.chargerlink.iot.common.connector.service.IChannelService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * @Author: ZH
 * @date Created on 14:37 2018/9/3.
 */
@Slf4j
@Component
@ChannelHandler.Sharable
@ConditionalOnProperty(name = TCPConnectorProperties.FLAG_PREFIX + ".enable", havingValue = "true")
public class ServerHandler extends ChannelInboundHandlerAdapter {

    @Autowired
    private IChannelService channelService;

    @Override
    public void channelRegistered(ChannelHandlerContext context) throws Exception {
        channelService.createChannel(context);
        super.channelActive(context);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext context) throws Exception {
        channelService.closeChannelById(context.channel().id().asLongText());
        super.channelInactive(context);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
        log.error(String.format("TCP连接异常[remoteAddress=%s]", context.channel().remoteAddress()), cause);
        channelService.closeChannelById(context.channel().id().asLongText());
    }

    @Override
    public void channelRead(ChannelHandlerContext context, Object message) throws Exception {
        channelService.readMessage(context.channel().id().asLongText(), message);
    }
}