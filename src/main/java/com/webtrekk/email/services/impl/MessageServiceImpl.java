package com.webtrekk.email.services.impl;

import com.webtrekk.email.dto.EmailAvro;
import com.webtrekk.email.dto.EmailDTO;
import com.webtrekk.email.messaging.EmailChannels;
import com.webtrekk.email.services.EmailService;
import com.webtrekk.email.services.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@EnableBinding(EmailChannels.class)
@RequiredArgsConstructor
@Slf4j
public class MessageServiceImpl implements MessageService {

//    private final KafkaTemplate<String, EmailAvro> emailKafkaTemplate;
    private final EmailService emailService;
    private final EmailChannels emailChannels;

//    @Value(value = "${kafka.email.topic.name}")
//    private String topicName;
//    @Value(value = "${kafka.email.retry.topic.name}")
//    private String retryTopicName;
//    @Value(value = "${kafka.email.fail.topic.name}")
//    private String failTopicName;

    @Override
    public void produce(EmailAvro message) {
        log.debug("::producing to [channel/message]: [{}/{}]", EmailChannels.EMAIL_OUTPUT, message);
        emailChannels.outboundEmails().send(MessageBuilder.withPayload(message).build());
//        final GenericMessage<String> hello = new GenericMessage<>("Hello");
//        emailChannels.outboundEmails().send(hello);
        log.debug("sent");
//        emailKafkaTemplate.send(topicName, message);
    }

//    @Override
    @StreamListener(EmailChannels.EMAIL_INPUT)
    public void consume(GenericMessage<byte[]> message) {
        log.debug("::consuming from [channel/message]: {}/{},", EmailChannels.EMAIL_INPUT, message);
        log.debug("");
        try {
            emailService.send(EmailDTO.builder().build());
            log.debug("::acknowledged message: {}", message);
        } catch (Exception exception) {
            log.warn("::reason for retry", exception);
//            retryOrFail(message);
        }
    }

//    @StreamListener(EmailChannels.EMAIL_INPUT)
    public void consume(String message) {
        log.debug("::consuming from [channel/message]: {}/{},", EmailChannels.EMAIL_INPUT, message);
        try {
            emailService.send(EmailDTO.builder().build());
            log.debug("::acknowledged message: {}", message);
        } catch (Exception exception) {
            log.warn("::reason for retry", exception);
//            retryOrFail(message);
        }
    }

    @Override
    public void retry(EmailAvro message) {
        log.debug("::retrying message: {}", message);

        sendOrRetry(message);
    }

    private void sendOrRetry(EmailAvro message) {
        try {
            emailService.send(EmailDTO.fromAvro(message));
            log.debug("::acknowledged message: {}", message);
        } catch (Exception exception) {
            log.warn("::reason for retry", exception);
//            retryOrFail(message);
        }
    }

    private void retryOrFail(EmailAvro message) {
        Integer retries = message.getRetries();
        if (retries > 0) {
            --retries;
            final EmailAvro retiedMessage = EmailAvro.newBuilder(message)
                    .setRetries(retries)
                    .build();
//            log.debug("::retrying to [topicName/message]: [{}/{}]", retryTopicName, retiedMessage);
//            emailKafkaTemplate.send(retryTopicName, retiedMessage);
        } else {
//            log.debug("::moving permanently to [topicName/message]: [{}/{}]", failTopicName, message);
//            emailKafkaTemplate.send(failTopicName, message);
        }
    }
}
