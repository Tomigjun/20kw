//package com.chargerlink;
//
//import com.alibaba.fastjson.JSON;
//import com.chargerlink.common.exception.ChargerlinkException;
//import com.chargerlink.iot.common.codec.service.CodecService;
//import com.chargerlink.iot.common.codec.service.entity.DecodeResult;
//import com.chargerlink.iot.common.connector.service.IChannelService;
//import com.chargerlink.iot.common.connector.service.ITCPMessageListener;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
///**
// * @author GISirFive
// * @date Create on 2019/12/6 20:45
// */
//@Slf4j
//@Component
//public class TCPMessageListener implements ITCPMessageListener {
//
//    @Autowired
//    private CodecService codecService;
//    @Autowired
//    private IChannelService channelService;
//    /**
//     * 收到消息
//     *
//     * @param message    消息体
//     * @param protocolId 协议ID
//     * @param channelId  通道ID
//     * @throws ChargerlinkException
//     */
//    @Override
//    public void onMessageReceived(byte[] message, String protocolId, String channelId) throws ChargerlinkException {
//        log.info("收到消息[body={}, protocolId={}, channelId={}]", message, protocolId, channelId);
//        DecodeResult result = codecService.decode(protocolId, message);
//        log.info("解码结果[result={}]", JSON.toJSONString(result));
//
//        //推送数据到设备
//        channelService.writeMessage(channelId, "嘿嘿".getBytes());
//
//    }
//}
