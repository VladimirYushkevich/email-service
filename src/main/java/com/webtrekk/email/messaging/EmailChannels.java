package com.webtrekk.email.messaging;

import com.webtrekk.email.dto.EmailAvro;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface EmailChannels {

    String OUT = "emails-out";
    String IN = "emails-in";

    String SEND_OUT = "emails-send-out";
    String SEND_IN = "emails-send-in";

    String RETRY_OUT = "emails-retry-out";
    String RETRY_IN = "emails-retry-in";

    String COUNT_OUT = "emails-count-out";
    String COUNT_IN = "emails-count-in";

    String STORE_OUT = "emails-store-out";
    String STORE_IN = "emails-store-in";

    String COUNT_MV = "emails-count-mv";
    String STATUS_MV = "emails-status-mv";

    @Output(OUT)
    MessageChannel outbound();

    @Input(IN)
    KStream<String, EmailAvro> inbound();

    @Output(RETRY_OUT)
    KStream<String, EmailAvro> outboundRetry();

    @Input(RETRY_IN)
    KStream<String, EmailAvro> inboundRetry();

    @Output(SEND_OUT)
    MessageChannel outboundSend();

    @Input(SEND_IN)
    KStream<String, EmailAvro> inboundSend();

    @Output(COUNT_OUT)
    KStream<String, EmailAvro> outboundCount();

    @Input(COUNT_IN)
    KStream<String, EmailAvro> inboundCount();

    @Output(STORE_OUT)
    KStream<String, Long> outboundStoreCount();

    @Output(STORE_IN)
    KStream<String, Long> inboundStoreCount();
}
