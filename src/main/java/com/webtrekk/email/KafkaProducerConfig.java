package com.webtrekk.email;

import com.webtrekk.email.dto.EmailAvro;
import com.webtrekk.email.serealization.EmailAvroSerealizer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Profile("!test")
@Slf4j
public class KafkaProducerConfig {

    @Value(value = "${kafka.host}")
    private String kafkaHost;

    @Bean
    public ProducerFactory<String, EmailAvro> emailProducerFactory() {
        log.info("::creating emailProducerFactory");
        Map<String, Object> producerProperties = new HashMap<>();
        producerProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaHost);
        producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, EmailAvroSerealizer.class);
        return new DefaultKafkaProducerFactory<>(producerProperties);
    }

    @Bean
    public KafkaTemplate<String, EmailAvro> emailKafkaTemplate() {
        log.info("::creating emailKafkaTemplate");
        return new KafkaTemplate<>(emailProducerFactory());
    }

}
