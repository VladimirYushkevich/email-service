package com.webtrekk.email.services.impl;

import com.webtrekk.email.client.SMTPClient;
import com.webtrekk.email.dto.EmailAvro;
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
    public EmailAvro send(EmailAvro message) {
        final EmailDTO email = EmailDTO.fromAvro(message);
        log.info("::sending email {}", email);
        final EmailAvro.Builder builder = EmailAvro.newBuilder(message)
                .setRetries(message.getRetries() - 1);
        if (sendSafe(email)) {
            log.info("::successfully sent email={} to SMTPClient", email);
            return builder
                    .setSuccess(true)
                    .build();
        }

        log.info("::failed sent email={} to SMTPClient", email);
        return builder.build();
    }

    private boolean sendSafe(EmailDTO email) {
        boolean res = false;
        try {
            res = emailClient.sendEmail(email);
        } catch (Exception e) {
            log.error("exception during send, reason {}", e);
        }
        return res;
    }
}
