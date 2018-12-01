package com.webtrekk.email;

import com.webtrekk.email.dto.EmailAvro;
import com.webtrekk.email.serealization.EmailAvroDeserealizer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.AbstractMessageListenerContainer;
import org.springframework.kafka.test.rule.KafkaEmbedded;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.util.Map;

@Configuration
@Profile("test")
@Slf4j
public class KafkaConsumerConfigIT {

    @Autowired
    private KafkaEmbedded kafkaEmbedded;

    private ConsumerFactory<String, EmailAvro> emailConsumerFactory(String groupId) {
        log.info("[TEST]::creating emailConsumerFactory for groupId='{}'", groupId);
        Map<String, Object> consumerProperties = KafkaTestUtils.consumerProps(groupId, "false", kafkaEmbedded);
        consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, EmailAvroDeserealizer.class);
        return new DefaultKafkaConsumerFactory<>(consumerProperties);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, EmailAvro> emailKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, EmailAvro> factory = new ConcurrentKafkaListenerContainerFactory<>();
        final String groupId = "email.test";
        factory.setConsumerFactory(emailConsumerFactory(groupId));
        factory.getContainerProperties().setAckMode(AbstractMessageListenerContainer.AckMode.MANUAL_IMMEDIATE);
        log.info("[TEST]::created ConcurrentKafkaListenerContainerFactory['{}']", groupId);
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, EmailAvro> emailRetryKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, EmailAvro> factory = new ConcurrentKafkaListenerContainerFactory<>();
        final String groupId = "email.test.retry";
        factory.setConsumerFactory(emailConsumerFactory(groupId));
        factory.getContainerProperties().setAckMode(AbstractMessageListenerContainer.AckMode.MANUAL_IMMEDIATE);
        log.info("[TEST]::created ConcurrentKafkaListenerContainerFactory['{}']", groupId);
        return factory;
    }

}
