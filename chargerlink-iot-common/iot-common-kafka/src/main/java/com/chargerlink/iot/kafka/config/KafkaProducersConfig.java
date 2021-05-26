package com.chargerlink.iot.kafka.config;

import lombok.Data;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author GISirFive
 * @date Created on 16:02 2019/5/30.
 */
@Data
@ConfigurationProperties(prefix = KafkaProducersConfig.FLAG_PREFIX)
@ConditionalOnProperty(name = KafkaProducersConfig.FLAG_PREFIX + ".enable", havingValue = "true")
public class KafkaProducersConfig {
    /**
     * 属性前缀
     */
    protected static final String FLAG_PREFIX = "kafka.producer";

    /**
     * 是否启用生产者
     */
    private boolean enable = false;
    /**
     * 服务集群地址
     */
    private String brokers;
    /**
     * 批处理大小
     */
    private int batchSize = 65536;

    private int lingerMsConfig = 1;

    private int bufferMemoryConfig = 524288;

    @Bean
    public KafkaTemplate<String, byte[]> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    private ProducerFactory<String, byte[]> producerFactory() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, batchSize);
        properties.put(ProducerConfig.LINGER_MS_CONFIG, lingerMsConfig);
        properties.put(ProducerConfig.BUFFER_MEMORY_CONFIG, bufferMemoryConfig);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class);

//        properties.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 10000);
//        //生产者将会等待来自服务器的任何确认，该记录将立即添加到套接字缓冲区并视为已发送
//        properties.put(ProducerConfig.ACKS_CONFIG, "0");
//        //无需重试
//        properties.put(ProducerConfig.RETRIES_CONFIG, 0);
//        //发送数据超时
//        properties.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 3000);
        return new DefaultKafkaProducerFactory<>(properties);
    }
}
