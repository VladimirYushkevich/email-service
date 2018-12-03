package com.webtrekk.email.services.impl;

import com.webtrekk.email.dto.EmailAvro;
import com.webtrekk.email.dto.EmailDTO;
import com.webtrekk.email.services.EmailService;
import com.webtrekk.email.services.MessageService;
import com.webtrekk.email.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

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
    public ListenableFuture<SendResult<String, EmailAvro>> produce(EmailAvro message) {
        log.debug("::producing to [topicName/message]: [{}/{}]", topicName, message);
        return emailKafkaTemplate.send(topicName, message);
    }

    @Override
    public void consume(EmailAvro message) {
        log.debug("::consuming message: {}", message);

        sendOrRetry(message);
    }

    @Override
    public void sendToRetry(EmailAvro message) {
        log.debug("::retrying message: {}", message);

        sendOrRetry(message);
    }

    private void sendOrRetry(EmailAvro message) {
        try {
            emailService.send(EmailDTO.fromAvro(message), FileUtils.decode(message.getFile()));
            log.debug("::acknowledged message: {}", message);
        } catch (Exception exception) {
            log.warn("::reason for sendToRetry", exception);
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
