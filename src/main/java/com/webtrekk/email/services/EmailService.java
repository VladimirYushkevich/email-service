package com.webtrekk.email.services;

import com.webtrekk.email.dto.EmailDTO;
import org.springframework.web.multipart.MultipartFile;

public interface EmailService {

    void send(EmailDTO message, MultipartFile file);
}
