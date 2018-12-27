package com.webtrekk.email.services;

import com.webtrekk.email.dto.EmailAvro;

import java.util.Map;

public interface MessageService {

    String send(EmailAvro message);

    Map<String, Long> counts();

    String status(String id);
}
