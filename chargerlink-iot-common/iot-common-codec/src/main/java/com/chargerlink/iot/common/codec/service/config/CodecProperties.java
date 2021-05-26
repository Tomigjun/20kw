package com.chargerlink.iot.common.codec.service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * IOT codec的配置项
 *
 * @author GISirFive
 * @date Created on 0:12 2019/9/17.
 */
@Data
@ConfigurationProperties(prefix = CodecProperties.FLAG_PREFIX)
public class CodecProperties {

    public static final String FLAG_PREFIX = "iot.codec.config";

    /**
     * 是否启动
     */
    private boolean enable = true;
    /**
     * 配置文件路径,list方式配置
     */
    private List<String> rulePath;

}
