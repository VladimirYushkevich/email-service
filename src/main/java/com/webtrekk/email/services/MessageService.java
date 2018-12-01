package com.webtrekk.email.services;

import com.webtrekk.email.dto.EmailAvro;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;

public interface MessageService {

    void produce(EmailAvro message);

    @KafkaListener(topics = "${kafka.email.topic.name}", containerFactory = "emailKafkaListenerContainerFactory")
    void consume(EmailAvro message, Acknowledgment acknowledgment);

    @KafkaListener(topics = "${kafka.email.retry.topic.name}", containerFactory = "emailRetryKafkaListenerContainerFactory")
    void retry(EmailAvro message, Acknowledgment acknowledgment);
}
