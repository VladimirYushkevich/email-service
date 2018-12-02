package com.webtrekk.email.serealization;

import com.webtrekk.email.dto.EmailAvro;
import org.junit.Before;
import org.junit.Test;

import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AvroSerdeTest {

    private EmailAvroSerde emailAvroSerde;
    private EmailAvro emailAvro;

    @Before
    public void setUp() {
        emailAvroSerde = new EmailAvroSerde();

        emailAvro = EmailAvro.newBuilder()
                .setFrom("from")
                .setSubject("subject")
                .setId("id")
                .setRetries(3)
                .build();
    }

    @Test
    public void shouldSerializeEmailUsingBinaryEncoder() {
        byte[] data = emailAvroSerde.serializer().serialize("", emailAvro);
        assertTrue(Objects.nonNull(data));
        assertTrue(data.length > 0);
    }

    @Test
    public void shouldSerializeAndDeSerializeEmailUsingBinaryEncoder() {
        byte[] data = emailAvroSerde.serializer().serialize("", emailAvro);
        EmailAvro emailAvroActual = emailAvroSerde.deserializer().deserialize("", data);

        assertEquals(emailAvroActual, emailAvro);
    }
}
