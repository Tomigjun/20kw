package com.chargerlink.iot.common.codec.service.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.chargerlink.iot.common.codec.rule.RuleLink;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

/**
 * IOT codec初始化
 *
 * @author GISirFive
 * @date Created on 0:58 2019/9/17.
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(CodecProperties.class)
@ConditionalOnProperty(name = CodecProperties.FLAG_PREFIX + ".enable", havingValue = "true", matchIfMissing = true)
public class RuleLinkManager implements InitializingBean {

    @Autowired
    private ApplicationContext context;

    private CodecProperties properties;
    /**
     * 存放规则引擎的集合
     */
    private HashMap<String, RuleLink> ruleLinkMap;

    public RuleLinkManager(CodecProperties properties) {
        this.properties = properties;
        ruleLinkMap = new HashMap<>();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            if (properties.getRulePath() == null || properties.getRulePath().isEmpty()) {
                throw new NullPointerException(String.format("未配置规则链[%s.rule-path]", CodecProperties.FLAG_PREFIX));
            }
            List<String> fileList = properties.getRulePath();
            for (String file : fileList) {
                Resource resource = context.getResource(file);
                if (!resource.exists()) {
                    throw new NullPointerException(String.format("未找到规则链配置文件[%s]", file));
                }
                JSONObject ruleLinkValue = JSON.parseObject(resource.getInputStream(), StandardCharsets.UTF_8, JSONObject.class,
                        // 自动关闭流
                        Feature.AutoCloseSource,
                        // 允许注释
                        Feature.AllowComment,
                        // 允许单引号
                        Feature.AllowSingleQuotes,
                        // 使用 Big decimal
                        Feature.UseBigDecimal);
                if (ruleLinkValue == null) {
                    throw new NullPointerException(String.format("规则链配置文件读取失败[%s]", file));
                }
                RuleLink ruleLink = new RuleLink(ruleLinkValue);
                if (ruleLinkMap.containsKey(ruleLink.getProtocolId())) {
                    throw new NullPointerException(String.format("检测到重名的规则链，协议ID不能重复[path=%s, protocolId=%s]", file, ruleLink.getProtocolId()));
                }
                ruleLinkMap.put(ruleLink.getProtocolId(), ruleLink);
            }
        } catch (Exception e) {
            log.error("协议解析组件初始化失败", e);
            SpringApplication.exit(context);
        }
    }

    /**
     * 根据协议ID获取规则链
     *
     * @param protocolId
     * @return
     */
    public RuleLink getRuleLink(String protocolId) {
        return ruleLinkMap.get(protocolId);
    }

}
