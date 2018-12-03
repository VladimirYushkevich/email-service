package com.webtrekk.email.configurations;

import com.webtrekk.email.dto.EmailAvro;
import com.webtrekk.email.serealization.EmailAvroDeserealizer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
@Profile("!test")
@Slf4j
public class KafkaConsumerConfig {

    @Value(value = "${kafka.host}")
    private String bootstrapAddress;

    private ConsumerFactory<String, EmailAvro> emailConsumerFactory(String groupId) {
        log.info("::creating emailConsumerFactory for groupId='{}', {}", groupId, bootstrapAddress);
        Map<String, Object> consumerProperties = new HashMap<>();
        consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, EmailAvroDeserealizer.class);
        return new DefaultKafkaConsumerFactory<>(consumerProperties);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, EmailAvro> emailKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, EmailAvro> factory = new ConcurrentKafkaListenerContainerFactory<>();
        final String groupId = "email";
        factory.setConsumerFactory(emailConsumerFactory(groupId));
        log.info("::created ConcurrentKafkaListenerContainerFactory['{}']", groupId);
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, EmailAvro> emailRetryKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, EmailAvro> factory = new ConcurrentKafkaListenerContainerFactory<>();
        final String groupId = "emailRetry";
        factory.setConsumerFactory(emailConsumerFactory(groupId));
        log.info("::created ConcurrentKafkaListenerContainerFactory['{}']", groupId);
        return factory;
    }

}
