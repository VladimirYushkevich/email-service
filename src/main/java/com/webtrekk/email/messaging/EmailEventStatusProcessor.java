package com.webtrekk.email.messaging;

import com.webtrekk.email.dto.EmailAvro;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Serialized;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import static com.webtrekk.email.utils.KafkaUtils.longFrom;

@Component
@EnableBinding(EmailChannels.class)
@Slf4j
public class EmailEventStatusProcessor {

    private final Materialized materializedStatus;

    public EmailEventStatusProcessor(@Qualifier("materializedStatus") Materialized materializedStatus) {
        this.materializedStatus = materializedStatus;
    }

    @StreamListener(EmailChannels.COUNT_IN)
    @SendTo(EmailChannels.STORE_OUT)
    @SuppressWarnings("unchecked")
    public KStream<String, Long> processStatuses(KStream<String, EmailAvro> messages) {
        log.info("::consuming from channel: {}", EmailChannels.COUNT_IN);
        return messages.mapValues(m -> longFrom(m.getSuccess()))
                .groupByKey(Serialized.with(Serdes.String(), Serdes.Long()))
                .reduce((aggValue, newValue) -> aggValue, materializedStatus)
                .toStream();
    }
}
