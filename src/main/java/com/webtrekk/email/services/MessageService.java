package com.webtrekk.email.services;

import com.webtrekk.email.dto.EmailAvro;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

public interface MessageService {

    ListenableFuture<SendResult<String, EmailAvro>> produce(EmailAvro message);

    @KafkaListener(topics = "${kafka.email.topic.name}", containerFactory = "emailKafkaListenerContainerFactory")
    void consume(EmailAvro message);

    @KafkaListener(topics = "${kafka.email.retry.topic.name}", containerFactory = "emailRetryKafkaListenerContainerFactory")
    void sendToRetry(EmailAvro message);
}
