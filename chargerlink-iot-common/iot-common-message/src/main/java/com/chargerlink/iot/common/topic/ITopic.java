package com.chargerlink.iot.common.topic;

/**
 * topic类型的统一约束，用于规范topic中的数据<br>
 * 各系统在维护自己的topic时，必须实现该接口<br>
 * 通过该接口，{@link TopicManager}会组织并管理应用中所有的topic，对外暴露统一的获取topic的接口<br>
 * 未实现该接口的topic将无法被{@link TopicManager}统一管理
 *
 * @author GISirFive
 * @date Create on 2018/6/6 15:21
 */
public interface ITopic {

    /**
     * 获取用户自定义的topicID，ID用来找到唯一的topic
     *
     * @return
     */
    String getId();

    /**
     * 获取原始的topic值
     *
     * @return
     */
    String getValue();

    /**
     * 获取topic的过滤标签<br>
     * 该属性可以让topic监听器只收取topic下携带特定过滤标签的消息
     *
     * @return
     */
    String getValueFilter();

    /**
     * 设置topic的过滤条件<br>
     * 该属性可以让消息在发送时携带一个过滤标签，消息接收方可以根据该标签过滤消息
     *
     * @param filter
     */
    void setValueFilter(String filter);

}
