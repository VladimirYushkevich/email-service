package com.webtrekk.email;

import com.webtrekk.email.dto.EmailAvro;
import com.webtrekk.email.dto.EmailDTO;
import com.webtrekk.email.serealization.EmailAvroSerealizer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.test.rule.KafkaEmbedded;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.util.Map;

@Configuration
@Profile("test")
@Slf4j
public class KafkaProducerConfigIT {

    @Autowired
    private KafkaEmbedded kafkaEmbedded;

    @Value(value = "${kafka.email.topic.name}")
    private String topicName;

    @Bean
    public ProducerFactory<String, EmailAvro> emailProducerFactory() {
        log.info("[TEST]::creating emailProducerFactory");
        final Map<String, Object> producerProperties = KafkaTestUtils.producerProps(kafkaEmbedded);
        producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, EmailAvroSerealizer.class);
        return new DefaultKafkaProducerFactory<>(producerProperties);
    }

    @Bean
    public KafkaTemplate<String, EmailAvro> emailKafkaTemplate() {
        log.info("[TEST]::creating emailKafkaTemplate");
        KafkaTemplate<String, EmailAvro> kafkaTemplate = new KafkaTemplate<>(emailProducerFactory());
        kafkaTemplate.setDefaultTopic(topicName);
        return kafkaTemplate;
    }

}
