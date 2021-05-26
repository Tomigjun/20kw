//package com.chargerlink;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.chargerlink.iot.codec.service.entity.base.BaseCodecResult;
//import io.netty.buffer.ByteBufUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
///**
// * @author GISirFive
// * @date Create on 2019/11/21 11:03
// */
//@Slf4j
//@Component
//public class CodecProducer {
//
//    @Autowired
//    private TestService testService;
//
//    @Scheduled(cron = "0/10 * * * * ?")
//    public void task1() {
//        byte[] body = ByteBufUtil.decodeHexDump("aa550800005440291423134400013000014445564943455f434841524745525f434c5f414100000077826001220501383938363034303331303138373035333835313001010201000000000000000000a0");
//        for (int i = 0; i < 10; i++) {
//            BaseCodecResult<JSONObject> result = testService.decode("8888", body);
//        }
////        log.info("单元测试结束>>>>>>result:{}", JSON.toJSONString(result));
//    }
//
////    @Scheduled(cron = "0/1 * * * * ?")
////    public void task2() {
////        byte[] body = ByteBufUtil.decodeHexDump("aa550800005440291423134400013000014445564943455f434841524745525f434c5f414100000077826001220501383938363034303331303138373035333835313001010201000000000000000000a0");
////        BaseCodecResult<JSONObject> result = testService.decode("8888", body);
////        log.info("单元测试结束>>>>>>result:{}", JSON.toJSONString(result));
////    }
//
//}
