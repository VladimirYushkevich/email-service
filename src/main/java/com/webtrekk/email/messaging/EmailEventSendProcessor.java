package com.webtrekk.email.messaging;

import com.webtrekk.email.dto.EmailAvro;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Predicate;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Component
@EnableBinding(EmailChannels.class)
@Slf4j
public class EmailEventSendProcessor {

    private Predicate<String, EmailAvro> isSent = (k, v) -> v.getSuccess() || v.getRetries() == 0;
    private Predicate<String, EmailAvro> isRetryable = (k, v) -> !v.getSuccess() && v.getRetries() > 0;

    @StreamListener(EmailChannels.SEND_IN)
    @SendTo({EmailChannels.COUNT_OUT, EmailChannels.RETRY_OUT})
    public KStream<String, EmailAvro>[] processSend(KStream<String, EmailAvro> messages) {
        log.info("::consuming from channel: {}", EmailChannels.SEND_IN);
        return messages.branch(isSent, isRetryable);
    }
}
