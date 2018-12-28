package com.webtrekk.email.messaging;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Serialized;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

import static com.webtrekk.email.utils.KafkaUtils.booleanStringFrom;

@Component
@EnableBinding(EmailChannels.class)
@Slf4j
public class EmailEventCountSink {

    private final Materialized materializedCount;

    public EmailEventCountSink(@Qualifier("materializedCount") Materialized materializedCount) {
        this.materializedCount = materializedCount;
    }

    @StreamListener(EmailChannels.STORE_IN)
    @SuppressWarnings("unchecked")
    public void processCounts(KStream<String, Long> counts) {
        log.info("::consuming from channel: {}", EmailChannels.STORE_IN);
        counts
                .map((k, v) -> new KeyValue<>(booleanStringFrom(v), "0"))
                .groupByKey(Serialized.with(Serdes.String(), Serdes.String()))
                .count(materializedCount)
                .toStream();
    }
}
