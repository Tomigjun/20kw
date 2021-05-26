package com.chargerlink.device.manager;

import java.nio.charset.StandardCharsets;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.chargerlink.iot.common.codec.constant.AnalyticalUnitEnum;
import com.chargerlink.iot.common.codec.constant.NodeTypeEnum;
import com.chargerlink.iot.common.codec.core.NodeCodec;
import com.chargerlink.iot.common.codec.node.base.Node;
import com.chargerlink.iot.common.codec.rule.RuleLink;
import com.chargerlink.iot.common.codec.service.CodecService;
import com.chargerlink.iot.common.codec.service.entity.EncodeResult;
import com.chargerlink.iot.common.codec.utils.ByteUtils;

import io.netty.buffer.ByteBufUtil;


@RunWith(SpringRunner.class)
@SpringBootTest
public class JSONTest {

    private Logger logger = LoggerFactory.getLogger(JSONTest.class);

    @Autowired
    private ApplicationContext context;
    @Autowired
    private CodecService codecService;

    public static void main(String[] args) {
        System.out.println(NodeTypeEnum.CHECK.name());
    }

//    @Test
//    public void initRuleLink() throws Exception {
//        logger.info("单元测试开始>>>>>>");
//
//        Resource resource = context.getResource("classpath:codec/ycDownDataAndBeat.json");
//        if (!resource.exists()) {
//            throw new NullPointerException("根据协议配置文件路径，未找到文件");
//        }
//        JSONObject configs = JSON.parseObject(resource.getInputStream(), StandardCharsets.UTF_8, JSONObject.class,
//                // 自动关闭流
//                Feature.AutoCloseSource,
//                // 允许注释
//                Feature.AllowComment,
//                // 允许单引号
//                Feature.AllowSingleQuotes,
//                // 使用 Big decimal
//                Feature.UseBigDecimal);
//        logger.info("单元测试结束>>>>>>result:{}", configs.toJSONString());
//    }
//
//    @Test
//    public void decode() throws Exception {
//        logger.info("单元测试开始>>>>>>");
//        byte[] body = ByteBufUtil.decodeHexDump("aa550800005440291423134400013000014445564943455f434841524745525f434c5f414100000077826001220501383938363034303331303138373035333835313001010201000000000000000000a0");
////        BaseCodecResult<JSONObject> result = codecService.decode("8888",body);
//        new Thread(() -> {
//                codecService.decode("8888", body);
//        }, "Thread-111111").start();
//        new Thread(() -> {
//            codecService.decode("8888", body);
//        }, "Thread-22222").start();
////        logger.info("单元测试结束>>>>>>result:{}", JSON.toJSONString(result));
//    }
    @Test
    public void encode() throws Exception{
    	JSONObject json = new JSONObject();
    	json.put("startFrame", "8989");
    	json.put("messageCode", "12012001");
    	json.put("deviceCode", "1111111111111111");
    	json.put("dataType","00");
    	json.put("transStatus", "01");
//    	json.put("check", "01");
    	json.put("tail", "0304");
//    	String json = '{"startFrame": "8989","messageCode": "12012001", "deviceCode": "1111111111111111", "dataType",0,"transStatus": 1}';
    	EncodeResult res = codecService.encode("8989", json);
    	logger.info("编码单元测是结束>>>>>>>>>>>>结果为:{}",JSON.toJSON(res));
    }
    @Test
    public void transMinus() {
    	byte[] bytes = ByteUtils.decodeHexDump("0x8068");
    	System.out.println(JSON.toJSON(bytes));
    }

}
