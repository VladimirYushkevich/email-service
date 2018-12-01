package com.webtrekk.email.services.impl;

import com.webtrekk.email.dto.EmailAvro;
import com.webtrekk.email.dto.EmailDTO;
import com.webtrekk.email.services.EmailService;
import com.webtrekk.email.services.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageServiceImpl implements MessageService {

    private final KafkaTemplate<String, EmailAvro> emailKafkaTemplate;
    private final EmailService emailService;

    @Value(value = "${kafka.email.topic.name}")
    private String topicName;
    @Value(value = "${kafka.email.retry.topic.name}")
    private String retryTopicName;
    @Value(value = "${kafka.email.fail.topic.name}")
    private String failTopicName;

    @Override
    public void produce(EmailAvro message) {
        log.debug("::producing to [topicName/message]: [{}/{}]", topicName, message);
        emailKafkaTemplate.send(topicName, message);
    }

    @Override
    public void consume(EmailAvro message, Acknowledgment acknowledgment) {
        log.debug("::consuming message: {}, ack: {}", message, acknowledgment);

        sendOrRetry(message, acknowledgment);
    }

    @Override
    public void retry(EmailAvro message, Acknowledgment acknowledgment) {
        log.debug("::retrying message: {}, ack: {}", message, acknowledgment);

        sendOrRetry(message, acknowledgment);
    }

    private void sendOrRetry(EmailAvro message, Acknowledgment acknowledgment) {
        try {
            emailService.send(EmailDTO.fromAvro(message));
            acknowledgment.acknowledge();
            log.debug("::acknowledged message: {}, ack: {}", message, acknowledgment);
        } catch (Exception exception) {
            log.warn("::reason for retry", exception);
            retry(message);
        }
    }

    private void retry(EmailAvro message) {
        Integer retries = message.getRetries();
        if (retries > 0) {
            --retries;
            final EmailAvro retiedMessage = EmailAvro.newBuilder(message)
                    .setRetries(retries)
                    .build();
            log.debug("::retrying to [topicName/message]: [{}/{}]", retryTopicName, retiedMessage);
            emailKafkaTemplate.send(retryTopicName, retiedMessage);
        } else {
            log.debug("::moving permanently to [topicName/message]: [{}/{}]", failTopicName, message);
            emailKafkaTemplate.send(failTopicName, message);
        }
    }
}
