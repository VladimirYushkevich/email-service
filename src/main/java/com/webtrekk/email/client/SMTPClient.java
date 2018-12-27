package com.webtrekk.email.client;

import com.webtrekk.email.dto.EmailDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@Slf4j
public class SMTPClient {

    public boolean sendEmail(EmailDTO emailDTO, MultipartFile file) {
        log.info("::sending email via dummy client: {}/{}", emailDTO, file);
        return System.currentTimeMillis() % 2 == 0;
    }
}
