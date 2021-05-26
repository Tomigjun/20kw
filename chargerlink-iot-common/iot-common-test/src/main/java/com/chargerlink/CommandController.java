package com.chargerlink;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chargerlink.iot.common.codec.service.CodecService;
import com.chargerlink.iot.common.codec.service.entity.DecodeResult;
import com.chargerlink.iot.common.codec.service.entity.EncodeResult;
import io.netty.buffer.ByteBufUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author GISirFive
 * @date Create on 2020/4/16 9:50
 */
@Slf4j
@RestController
public class CommandController {

    @Autowired
    private CodecService codecService;

    /**
     * @param request
     */
    @PostMapping("/sendCommand")
    public void sendCommand(@RequestBody JSONObject request) {
        log.info("编码参数{}", request.toJSONString());
        EncodeResult result = codecService.encode("332833", request);
        log.info("编码结果{}", JSON.toJSONString(result));
    }

    /**
     * @param request
     */
    @PostMapping("/decode")
    public void decode(String request) {
        log.info("解码参数{}", request);
        byte[] body = ByteBufUtil.decodeHexDump(request);
        DecodeResult result = codecService.decode("332833", body);
//        DecodeResult result = codecService.decode("18889", body);
        log.info("解码结果{}", JSON.toJSONString(result));
    }

}
