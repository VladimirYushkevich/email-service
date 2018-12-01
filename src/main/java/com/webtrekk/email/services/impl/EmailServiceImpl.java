package com.webtrekk.email.services.impl;

import com.webtrekk.email.client.SMTPClient;
import com.webtrekk.email.dto.EmailDTO;
import com.webtrekk.email.services.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final SMTPClient emailClient;

    @Override
    public void send(EmailDTO message) {
        log.info("::sending email={} to SMTPClient", message);
        emailClient.sendEmail(message);
    }
}
