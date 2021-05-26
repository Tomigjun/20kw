package com.chargerlink.iot.common.config;

import com.chargerlink.iot.common.util.CodeGenerator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 全局配置参数
 *
 * @author GISirFive
 * @date Create on 2019/5/24 18:00
 */
@Configuration
@ConfigurationProperties(prefix = IOTCommonProperties.FLAG_PREFIX)
public class IOTCommonProperties {

    public static final String FLAG_PREFIX = "iot";

    /**
     * nodeId的值
     */
    public static final String FLAG_NODE_ID_AUTO = "auto";

    /**
     * 项目名称，若未指定，则使用${spring.application.name}
     */
    private String projectName;
    /**
     * 应用名称
     */
    @Value("${spring.application.name}")
    private String applicationName;
    /**
     * 当前节点的ID(默认为auto，即系统自动生成)
     */
    private String nodeId = FLAG_NODE_ID_AUTO;

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void setNodeId(String nodeId) {
        if (StringUtils.isBlank(nodeId) || FLAG_NODE_ID_AUTO.equals(nodeId)) {
            nodeId = CodeGenerator.generateNodeId();
        }
        this.nodeId = nodeId;
    }

    /**
     * 获取项目名称<br>
     * 若未指定，则使用spring.application.name
     *
     * @return
     */
    public String getProjectName() {
        if (StringUtils.isBlank(projectName)) {
            projectName = applicationName;
        }
        return projectName;
    }

    /**
     * 当前节点的ID(默认为auto，即系统自动生成)
     *
     * @return
     */
    public String getNodeId() {
        if (StringUtils.isBlank(nodeId) || FLAG_NODE_ID_AUTO.equals(nodeId)) {
            nodeId = CodeGenerator.generateNodeId();
        }
        return nodeId;
    }
}
