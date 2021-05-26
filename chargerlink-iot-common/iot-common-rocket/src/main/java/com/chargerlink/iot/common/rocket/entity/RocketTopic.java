package com.chargerlink.iot.common.rocket.entity;

import com.chargerlink.iot.common.topic.ITopic;
import lombok.Data;

/**
 * Rocket支持的Topic
 *
 * @author GISirFive
 * @date Create on 2019/6/24 10:36
 */
@Data
public class RocketTopic implements ITopic {

    /**
     * ID
     */
    private String id;
    /**
     * topic值 <b>命名规则:ApplicationName-TopicType-MessageDirection</b><br>
     * {@link com.chargerlink.iot.common.constant.TopicTypeEnum}<br>
     * {@link com.chargerlink.iot.common.constant.MessageDirectionConst}
     */
    private String value;
    /**
     * topic描述
     */
    private String description;
    /**
     * 组名称，默认为父节点中的default-group-name
     */
    private String groupName;
    /**
     * *代表topic下的所有消息(默认)，auto代表使用节点ID(${iot.node-id})，或自定义
     */
    private String tags = "*";

    /**
     * 获取topic的过滤标签<br>
     * 该属性可以让topic监听器只收取topic下携带特定过滤标签的消息
     *
     * @return
     */
    @Override
    public String getValueFilter() {
        return tags;
    }

    /**
     * 设置topic的过滤条件<br>
     * 该属性可以让消息在发送时携带一个过滤标签，消息接收方可以根据该标签过滤消息
     *
     * @param filter
     */
    @Override
    public void setValueFilter(String filter) {
        this.tags = filter;
    }
}
