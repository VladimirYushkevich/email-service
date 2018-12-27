package com.webtrekk.email.messaging;

import com.webtrekk.email.dto.EmailAvro;
import com.webtrekk.email.services.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

import static com.webtrekk.email.utils.KafkaUtils.buildMessage;

@Component
@EnableBinding(EmailChannels.class)
@RequiredArgsConstructor
@Slf4j
public class EmailEventRetrySink {

    private final EmailService emailService;
    private final EmailChannels emailChannels;

    @StreamListener(EmailChannels.RETRY_IN)
    public void processRetries(KStream<String, EmailAvro> messages) {
        log.info("::consuming from channel: {}", EmailChannels.RETRY_IN);
        messages.foreach((k, v) -> send(v));
    }

    private void send(EmailAvro message) {
        final EmailAvro sentMessage = emailService.send(message);
        emailChannels.outboundSend().send(buildMessage(sentMessage));
        log.info("::sent to [channel/message]: [{}/{}] for dispatching", EmailChannels.SEND_OUT, sentMessage);
    }
}
