package com.chargerlink.iot.common.connector.config;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

/**
 * TCP连接配置
 *
 * @author GISirFive
 * @date Create on 2019/11/28 15:38
 */
@Data
@ConfigurationProperties(prefix = TCPConnectorProperties.FLAG_PREFIX)
public class TCPConnectorProperties {

    public static final String FLAG_PREFIX = "iot.connector";

    /**
     * 是否启动
     */
    private boolean enable = false;
    /**
     * 协议配置
     */
    @NestedConfigurationProperty
    private ProtocolConfig protocolConfig;
    /**
     * netty配置
     */
    @NestedConfigurationProperty
    private NettyConfig nettyConfig;

    /**
     * 协议配置
     */
    @Data
    public static class ProtocolConfig {
        /**
         * 协议ID
         */
        private String protocolId;
        /**
         * 协议配置文件路径
         */
        private String location;
    }

    /**
     * netty配置
     */
    @Data
    public static class NettyConfig {
        /**
         * 服务端端口号
         */
        private Integer serverPort;

    }
}
