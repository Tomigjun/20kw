package com.chargerlink.iot.common.connector.service;

import com.alibaba.fastjson.JSON;
import com.chargerlink.common.exception.ChargerlinkException;
import com.chargerlink.iot.common.connector.config.TCPConnectorProperties;
import com.chargerlink.iot.common.connector.entity.TCPSession;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Channel管理
 *
 * @author GISirFive
 * @date Create on 2020/3/12 15:34
 */
@Slf4j
@Component
@ConditionalOnProperty(name = TCPConnectorProperties.FLAG_PREFIX + ".enable", havingValue = "true")
public class ChannelServiceImpl implements IChannelService, InitializingBean {

    @Autowired
    private ApplicationContext context;
    @Autowired
    private TCPConnectorProperties properties;
    /**
     * TCP会话管理，key是channelId，value是{@link TCPSession}
     */
    private ConcurrentHashMap<String, TCPSession> sessionMap;
    /**
     * 消息监听
     */
    private List<ITCPMessageListener> messageListeners = new ArrayList<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        sessionMap = new ConcurrentHashMap<>();
        Map<String, ITCPMessageListener> listenerMap = context.getBeansOfType(ITCPMessageListener.class, true, true);
        if (listenerMap.isEmpty()) {
            log.error("未检测到TCP上行消息监听器[com.chargerlink.iot.common.connector.service.ITCPMessageListener]的bean");
        } else {
            log.info("检测到TCP上行消息监听器[listener={}]", listenerMap.toString());
        }
        messageListeners.addAll(listenerMap.values());
    }

    /**
     * 新增tcp会话
     *
     * @param context
     */
    @Override
    public void createChannel(ChannelHandlerContext context) throws ChargerlinkException {
        String clientId = context.channel().id().asLongText();
        TCPSession session = new TCPSession();
        session.setClientId(clientId);
        session.setContext(context);
        session.setCreateTime(System.currentTimeMillis());
        sessionMap.put(clientId, session);
        log.info("新增tcp会话[session={}]", JSON.toJSONString(session));
    }

    /**
     * 关闭tcp会话
     *
     * @param context
     */
    @Override
    public void closeChannel(ChannelHandlerContext context) throws ChargerlinkException {
        if (!context.isRemoved()) {
            context.close();
        }
        String channelId = context.channel().id().asLongText();
        closeChannelById(channelId);
        log.info("关闭TCP会话[channelId={}]", channelId);
    }

    /**
     * 关闭TCP会话
     *
     * @param channelId
     */
    @Override
    public void closeChannelById(String channelId) throws ChargerlinkException {
        TCPSession session = sessionMap.get(channelId);
        if (session == null) {
            return;
        }
        sessionMap.remove(channelId);
        if (!session.getContext().isRemoved()) {
            session.getContext().close();
        }
        log.info("关闭TCP会话[session={}]", JSON.toJSONString(session));
    }

    /**
     * 读取消息
     *
     * @param channelId
     * @param message
     */
    @Override
    public void readMessage(String channelId, Object message) throws ChargerlinkException {
        byte[] messageBody = new byte[0];
        if (message instanceof ByteBuf) {
            ByteBuf byteBuf = (ByteBuf) message;
            messageBody = ByteBufUtil.getBytes(byteBuf);
            byteBuf.release();
        }
        log.debug("接收到TCP消息[channelId={}, message={}]", channelId, ByteBufUtil.hexDump(messageBody));
        String protocolId = properties.getProtocolConfig().getProtocolId();
        try {
            //FIXME 此处应该每一个listener都持有一个单独的消息分发器，避免前面的listener抛错，导致后面的listener接收不到消息
            for (ITCPMessageListener listener : messageListeners) {
                listener.onMessageReceived(messageBody, protocolId, channelId);
            }
        } catch (Exception e) {
        }
    }

    /**
     * 写入消息
     *
     * @param channelId
     * @param message
     */
    @Override
    public void writeMessage(String channelId, byte[] message) throws ChargerlinkException {
        TCPSession session = sessionMap.get(channelId);
        if (session == null) {
            log.error("下行消息发送失败,根据连接ID未找到TCP会话[channelId={},message={}]", channelId, ByteBufUtil.hexDump(message));
            throw new ChargerlinkException("根据连接ID未找到TCP会话");
        }
        ChannelHandlerContext context = session.getContext();
        if (context == null) {
            closeChannelById(channelId);
            log.error("下行消息发送失败,根据连接ID未找到TCP会话[channelId={},message={}]", channelId, ByteBufUtil.hexDump(message));
            throw new ChargerlinkException("根据连接ID未找到TCP会话");
        }
        if (context.isRemoved()) {
            closeChannelById(channelId);
            log.error("下行消息发送失败,TCP会话已关闭[channelId={},message={}]", channelId, ByteBufUtil.hexDump(message));
            throw new ChargerlinkException("TCP会话已关闭");
        }
        //使用EventLoop的任务调度发送下行
        context.channel().eventLoop().execute(() -> {
            //与context.channel().writeAndFlush(message) 相比这种方式不必经过所有的handler
            context.channel().writeAndFlush(message);
        });
    }

    @Override
    public Integer getActivateChannelCount() {
        int count = 0;
        for (TCPSession session : sessionMap.values()) {
            if (session.getContext().channel().isActive()) {
                count++;
            }
        }
        return count;
    }

}
