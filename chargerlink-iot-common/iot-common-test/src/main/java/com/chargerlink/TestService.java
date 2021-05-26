//package com.chargerlink;
//
//import com.alibaba.fastjson.JSONObject;
//import com.chargerlink.iot.codec.service.CodecService;
//import com.chargerlink.iot.codec.service.entity.base.BaseCodecResult;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Service;
//
///**
// * @author GISirFive
// * @date Create on 2020/3/27 14:28
// */
//@Service
//public class TestService {
//
//
//    @Autowired
//    private CodecService codecService;
//
//    /**
//     * 消息解码
//     *
//     * @param protocolId 设备消息协议ID
//     * @param data       消息体
//     * @return
//     */
//    @Async
//    public BaseCodecResult<JSONObject> decode(String protocolId, byte[] data) {
//        return codecService.decode(protocolId, data, 0);
//    }
//
//}
