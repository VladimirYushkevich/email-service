package com.webtrekk.email.messaging;

import com.webtrekk.email.dto.EmailAvro;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

import static com.webtrekk.email.utils.KafkaUtils.buildMessage;

@Component
@EnableBinding(EmailChannels.class)
@ConfigurationProperties(prefix = "email")
@Getter
@Setter
@RequiredArgsConstructor
@Slf4j
public class EmailEventSink {

    private int retries;
    private final EmailChannels emailChannels;

    @StreamListener(EmailChannels.IN)
    public void processAll(KStream<String, EmailAvro> messages) {
        log.info("::consuming from channel: {}", EmailChannels.IN);
        messages.foreach((k, v) -> send(v));
    }

    private void send(EmailAvro message) {
        final EmailAvro messageWithRetries = EmailAvro.newBuilder(message)
                .setRetries(retries)
                .build();
        emailChannels.outboundSend().send(buildMessage(messageWithRetries));
        log.info("::sent to [channel/message]: [{}/{}]", EmailChannels.SEND_OUT, messageWithRetries);
    }

}
