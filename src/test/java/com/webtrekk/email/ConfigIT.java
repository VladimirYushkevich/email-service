package com.webtrekk.email;

import com.webtrekk.email.client.SMTPClient;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class ConfigIT {

    @Bean
    @Primary
    public SMTPClient smtpClient() {
        return Mockito.mock(SMTPClient.class);
    }
}
