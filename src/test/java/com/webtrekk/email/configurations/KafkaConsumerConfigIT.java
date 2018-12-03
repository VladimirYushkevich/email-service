package com.webtrekk.email.configurations;

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
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.util.Map;

@Configuration
@Profile("test")
@Slf4j
public class KafkaConsumerConfigIT {

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    private ConsumerFactory<String, EmailAvro> emailConsumerFactory(String groupId) {
        log.info("[TEST]::creating emailConsumerFactory for groupId='{}'", groupId);
        Map<String, Object> consumerProperties = KafkaTestUtils.consumerProps(groupId, "false", embeddedKafkaBroker);
        consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, EmailAvroDeserealizer.class);
        return new DefaultKafkaConsumerFactory<>(consumerProperties);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, EmailAvro> emailKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, EmailAvro> factory = new ConcurrentKafkaListenerContainerFactory<>();
        final String groupId = "email.test";
        factory.setConsumerFactory(emailConsumerFactory(groupId));
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        log.info("[TEST]::created ConcurrentKafkaListenerContainerFactory['{}']", groupId);
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, EmailAvro> emailRetryKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, EmailAvro> factory = new ConcurrentKafkaListenerContainerFactory<>();
        final String groupId = "email.test.sendToRetry";
        factory.setConsumerFactory(emailConsumerFactory(groupId));
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        log.info("[TEST]::created ConcurrentKafkaListenerContainerFactory['{}']", groupId);
        return factory;
    }

}
