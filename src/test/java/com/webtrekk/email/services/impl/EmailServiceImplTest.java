package com.webtrekk.email.services.impl;

import com.webtrekk.email.client.SMTPClient;
import com.webtrekk.email.dto.EmailAvro;
import com.webtrekk.email.services.EmailService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.webtrekk.email.TestUtils.getEmailAvroEvent;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
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
        when(emailClientMock.sendEmail(any(), any())).thenReturn(true);

        final EmailAvro emailAvro = emailService.send(EmailAvro.newBuilder(getEmailAvroEvent())
                .setRetries(3)
                .build());

        verify(emailClientMock, times(1)).sendEmail(any(), any());
        assertTrue(emailAvro.getSuccess());
        assertThat(emailAvro.getRetries(), is(2));
    }

    @Test
    public void shouldUseRetryDuringException() {
        when(emailClientMock.sendEmail(any(), any())).thenThrow(new RuntimeException("from Test"));

        final EmailAvro emailAvro = emailService.send(EmailAvro.newBuilder(getEmailAvroEvent())
                .setRetries(3)
                .build());

        verify(emailClientMock, times(1)).sendEmail(any(), any());
        assertFalse(emailAvro.getSuccess());
        assertThat(emailAvro.getRetries(), is(2));
    }

    @Test
    public void shouldUseRetryAfterFail() {
        when(emailClientMock.sendEmail(any(), any())).thenReturn(false);

        final EmailAvro emailAvro = emailService.send(EmailAvro.newBuilder(getEmailAvroEvent())
                .setRetries(3)
                .build());

        verify(emailClientMock, times(1)).sendEmail(any(), any());
        assertFalse(emailAvro.getSuccess());
        assertThat(emailAvro.getRetries(), is(2));
    }
}
