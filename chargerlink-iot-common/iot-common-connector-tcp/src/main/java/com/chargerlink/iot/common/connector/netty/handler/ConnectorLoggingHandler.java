package com.chargerlink.iot.common.connector.netty.handler;

import com.chargerlink.iot.common.connector.config.TCPConnectorProperties;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Connector日志输出
 *
 * @author GISirFive
 * @date Created on 15:12 2019/4/11.
 */
@Slf4j
@Component
@ChannelHandler.Sharable
@ConditionalOnProperty(name = TCPConnectorProperties.FLAG_PREFIX + ".enable", havingValue = "true")
public class ConnectorLoggingHandler extends LoggingHandler {


    public ConnectorLoggingHandler() {
        //日志级别
        super(log.isDebugEnabled() ? LogLevel.DEBUG : (log.isInfoEnabled() ? LogLevel.INFO : LogLevel.WARN));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ByteBuf) {
            log.debug("READ[messageBody={},remoteIp={},channelId={}]", ByteBufUtil.hexDump((ByteBuf) msg), ctx.channel().remoteAddress(), ctx.channel().id().asLongText());
        } else {
            logger.log(internalLevel, format(ctx, "READ", msg));
        }
        ctx.fireChannelRead(msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof ByteBuf) {
            log.debug("WRITE[messageBody={},remoteIp={},channelId={}]", ByteBufUtil.hexDump((ByteBuf) msg), ctx.channel().remoteAddress(), ctx.channel().id().asLongText());
        } else {
            logger.log(internalLevel, format(ctx, "WRITE", msg));
        }
        ctx.write(msg, promise);
    }
}
