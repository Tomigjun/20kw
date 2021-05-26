package com.chargerlink.iot.common.topic;

import com.alibaba.fastjson.JSON;
import com.chargerlink.iot.common.config.IOTCommonProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * topic管理
 *
 * @author GISirFive
 * @date Create on 2020/3/2 10:58
 */
@Slf4j
@Configuration
@AutoConfigureAfter({ITopicProperties.class, IOTCommonProperties.class})
public class TopicManager implements InitializingBean {

    @Autowired
    private ApplicationContext context;
    @Autowired
    private IOTCommonProperties commonProperties;

    /**
     * topic列表
     */
    private List<ITopic> topicList;
    /**
     * 根据topicId索引的Map，key为topicId，value为topic
     */
    private Map<String, ITopic> topicOfIdMap = new HashMap<>();

    public TopicManager() {
        //初始化topic列表
        topicList = new ArrayList<>();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //NOTE 初始化topic列表
        Map<String, ITopicProperties> topicPropertiesMap = context.getBeansOfType(ITopicProperties.class, true, true);
        for (ITopicProperties properties : topicPropertiesMap.values()) {
            if (properties.getCompleteTopics() != null) {
                topicList.addAll(properties.getCompleteTopics());
            }
        }
        Map<String, ITopic> topicMap = context.getBeansOfType(ITopic.class, true, true);
        topicList.addAll(topicMap.values());

        //NOTE 校验topic基础字段
        for (ITopic topic : topicList) {
            if (StringUtils.isBlank(topic.getId())) {
                log.error("Topic检查未通过", new NullPointerException(String.format("id不能为空[class=%s, topic=%s]",
                        topic.getClass().getName(), JSON.toJSONString(topic))));
                SpringApplication.exit(context);
            }
            if (StringUtils.isBlank(topic.getValue())) {
                log.error("Topic检查未通过", new NullPointerException(String.format("value不能为空[class=%s, topic=%s]",
                        topic.getClass().getName(), JSON.toJSONString(topic))));
                SpringApplication.exit(context);
            }
            //NOTE 如果filter为auto，则使用nodeId
            if (topic.getValueFilter() != null && IOTCommonProperties.FLAG_NODE_ID_AUTO.equals(topic.getValueFilter())) {
                //如果filter为auto，则使用nodeId
                topic.setValueFilter(commonProperties.getNodeId());
            }
            topicOfIdMap.put(topic.getId(), topic);
        }
    }


    /**
     * 根据ID获取topic
     *
     * @param topicId
     * @return
     */
    public ITopic getTopicById(String topicId) {
        ITopic topic = topicOfIdMap.get(topicId);
        if (topic == null) {
            return null;
        }
        //FIXME 此处使用JSON转换实现对象的深拷贝，在大量调用该方法时会耗时严重，影响性能
        return JSON.parseObject(JSON.toJSONString(topic), topic.getClass());
    }

    /**
     * 根据ID获取topic，并设置新的过滤条件
     *
     * @param topicId
     * @param newFilter 新的过滤条件
     * @return
     */
    public ITopic getTopicOfNewFilter(String topicId, String newFilter) {
        ITopic topic = getTopicById(topicId);
        if (topic == null) {
            return null;
        }
        topic.setValueFilter(newFilter);
        return topic;
    }
}
