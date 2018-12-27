package com.webtrekk.email;

import com.webtrekk.email.dto.EmailAvro;

import java.util.UUID;

public class TestUtils {

    public static EmailAvro getEmailAvroEvent() {
        return EmailAvro.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setFrom("from")
                .setSubject("subject")
                .build();
    }
}
