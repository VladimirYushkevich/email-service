package com.webtrekk.email.services;

import com.webtrekk.email.dto.EmailDTO;

public interface EmailService {

    void send(EmailDTO message);
}
