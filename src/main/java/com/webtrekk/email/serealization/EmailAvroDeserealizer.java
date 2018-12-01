package com.webtrekk.email.serealization;

import com.webtrekk.email.dto.EmailAvro;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;
import java.util.Map;

@Slf4j
public class EmailAvroDeserealizer implements Deserializer<EmailAvro> {

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        log.debug("::configure");
    }

    @Override
    public EmailAvro deserialize(String topic, byte[] data) {
        DatumReader<EmailAvro> reader = new SpecificDatumReader<>(EmailAvro.class);
        Decoder decoder = DecoderFactory.get().binaryDecoder(data, null);
        try {
            return reader.read(null, decoder);
        } catch (IOException e) {
            log.error("Deserialization error: {}", e.getMessage());
        }
        return null;
    }

    @Override
    public void close() {
        log.debug("::close");
    }
}
