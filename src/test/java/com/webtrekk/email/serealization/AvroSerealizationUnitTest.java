package com.webtrekk.email.serealization;

import com.webtrekk.email.dto.EmailAvro;
import org.junit.Before;
import org.junit.Test;

import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AvroSerealizationUnitTest {

    private EmailAvroSerealizer emailAvroSerealizer;
    private EmailAvroDeserealizer emailAvroDeserealizer;
    private EmailAvro emailAvro;

    @Before
    public void setUp() {
        emailAvroSerealizer = new EmailAvroSerealizer();
        emailAvroDeserealizer = new EmailAvroDeserealizer();

        emailAvro = EmailAvro.newBuilder()
                .setFrom("from")
                .setSubject("subject")
                .setId("id")
                .setFile("encodedFile")
                .setRetries(3)
                .build();
    }

    @Test
    public void shouldSerializeEmailUsingBinaryEncoder() {
        byte[] data = emailAvroSerealizer.serialize("", emailAvro);
        assertTrue(Objects.nonNull(data));
        assertTrue(data.length > 0);
    }

    @Test
    public void shouldSerializeAndDeSerializeEmailUsingBinaryEncoder() {
        byte[] data = emailAvroSerealizer.serialize("", emailAvro);
        EmailAvro emailAvroActual = emailAvroDeserealizer.deserialize("", data);

        assertEquals(emailAvroActual, emailAvro);
    }
}
