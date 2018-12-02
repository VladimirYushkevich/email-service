package com.webtrekk.email.services;

import com.webtrekk.email.dto.EmailAvro;

public interface MessageService {

    //    @SendTo(EmailChannels.EMAIL_OUTPUT)
    void produce(EmailAvro message);

    //    @KafkaListener(topics = "${kafka.email.topic.name}", containerFactory = "emailKafkaListenerContainerFactory")
//    void consume(EmailAvro message);

    //    @KafkaListener(topics = "${kafka.email.retry.topic.name}", containerFactory = "emailRetryKafkaListenerContainerFactory")
    void retry(EmailAvro message);
}
