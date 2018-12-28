package com.webtrekk.email.messaging;

import com.webtrekk.email.dto.EmailAvro;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.stereotype.Component;

import static com.webtrekk.email.utils.KafkaUtils.buildMessage;

@Component
@EnableBinding(EmailChannels.class)
@RequiredArgsConstructor
@Slf4j
public class EmailEventSource {

    private final EmailChannels emailChannels;

    public String produce(EmailAvro message) {
        emailChannels.outbound().send(buildMessage(message));
        log.info("::sent to [channel/message]: [{}/{}]", EmailChannels.OUT, message);
        return message.getId();
    }
}
