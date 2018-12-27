package com.webtrekk.email.services;

import com.webtrekk.email.dto.EmailAvro;

import java.util.Map;

public interface MessageService {

    String send(EmailAvro message);

    Map<String, Long> stats();

    String status(String id);
}
