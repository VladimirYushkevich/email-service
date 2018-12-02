package com.webtrekk.email.serealization;

import com.webtrekk.email.dto.EmailAvro;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

@Slf4j
public class EmailAvroSerde implements Serde<EmailAvro> {

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        log.debug("::configure");
    }

    @Override
    public void close() {
        log.debug("::close");
    }

    @Override
    public Serializer<EmailAvro> serializer() {
        return new EmailAvroSerealizer();
    }

    @Override
    public Deserializer<EmailAvro> deserializer() {
        return new EmailAvroDeserealizer();
    }
}
