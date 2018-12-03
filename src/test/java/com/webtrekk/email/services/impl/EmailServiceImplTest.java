package com.webtrekk.email.services.impl;

import com.webtrekk.email.client.SMTPClient;
import com.webtrekk.email.services.EmailService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class EmailServiceImplTest {

    @Mock
    private SMTPClient emailClientMock;

    private EmailService emailService;

    @Before
    public void setUp() {
        emailService = new EmailServiceImpl(emailClientMock);
    }

    @Test
    public void shouldSendEmailWithoutRetries() {
        doNothing().when(emailClientMock).sendEmail(any(), any());

        emailService.send(any(), any());

        verify(emailClientMock, times(1)).sendEmail(any(), any());
    }

    @Test(expected = RuntimeException.class)
    public void shouldUseRetryDuringException() {
        doThrow(new RuntimeException("from IT")).when(emailClientMock).sendEmail(any(), any());

        emailService.send(any(), any());

        verify(emailClientMock, times(2)).sendEmail(any(), any());
    }

}
