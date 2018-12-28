package com.webtrekk.email.services;

import com.webtrekk.email.dto.EmailAvro;

public interface EmailService {

    EmailAvro send(EmailAvro message);
}
