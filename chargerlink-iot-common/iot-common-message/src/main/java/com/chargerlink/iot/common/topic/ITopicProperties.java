package com.chargerlink.iot.common.topic;

import java.util.List;

/**
 * topic属性接口，所有定义topic的类都需要实现该接口<br>
 * 通过该接口，{@link TopicManager}会组织并管理应用中所有的topic，对外暴露统一的获取topic的接口<br>
 * 未实现该接口的类，其中的topic将无法统一管理
 *
 * @author GISirFive
 * @date Create on 2020/3/2 10:55
 */
public interface ITopicProperties {


    /**
     * 获取完整的topic列表，在ITopic中定义的字段要确保完整，且在系统运行过程中不会修改
     *
     * @return
     */
    List<? extends ITopic> getCompleteTopics();
}
