package com.webtrekk.email.serealization;

import com.webtrekk.email.dto.EmailAvro;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.kafka.common.serialization.Serializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

@Slf4j
public class EmailAvroSerealizer implements Serializer<EmailAvro> {

    @Override
    public void configure(Map configs, boolean isKey) {
        log.debug("::configure");
    }

    @Override
    public byte[] serialize(String topic, EmailAvro emailAvro) {
        DatumWriter<EmailAvro> writer = new SpecificDatumWriter<>(EmailAvro.class);
        byte[] data = new byte[0];
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Encoder jsonEncoder = EncoderFactory.get().binaryEncoder(stream, null);
        try {
            writer.write(emailAvro, jsonEncoder);
            jsonEncoder.flush();
            data = stream.toByteArray();
        } catch (IOException e) {
            log.error("Serialization error: {}", e.getMessage());
        }

        return data;
    }

    @Override
    public void close() {
        log.debug("::close");
    }
}
