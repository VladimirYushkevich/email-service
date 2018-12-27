package com.webtrekk.email.client;

import com.webtrekk.email.dto.EmailDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SMTPClient {

    public boolean sendEmail(EmailDTO emailDTO) {
        log.info("::sending email via dummy client: {}", emailDTO);
        return System.currentTimeMillis() % 2 == 0;
    }
}
