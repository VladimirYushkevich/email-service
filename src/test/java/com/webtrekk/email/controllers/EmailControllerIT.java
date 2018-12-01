package com.webtrekk.email.controllers;

import com.webtrekk.email.BaseIT;
import com.webtrekk.email.dto.EmailAvro;
import com.webtrekk.email.dto.EmailDTO;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.ACCEPTED;

public class EmailControllerIT extends BaseIT {

    @Value("http://localhost:${local.server.port}/api/v1/email")
    private String base;

    private final int NUMBER_OF_RETRIES = 2;

    private ArgumentCaptor<EmailDTO> emailCaptor = ArgumentCaptor.forClass(EmailDTO.class);

    @Test
    public void sendEmailDTOShouldReturnSuccessAcknowledgmentAndConsumeWithoutRetries() throws Exception {
        doNothing().when(emailClientMock).sendEmail(any());

        ResponseEntity<Void> responseEntity = template.postForEntity(base,
                EmailDTO.builder()
                        .from("from@example.com")
                        .retries(NUMBER_OF_RETRIES)
                        .build(),
                Void.class);

        //allow to consume
        Thread.sleep(delayForConsumer);
        verify(emailClientMock, times(1)).sendEmail(any());

        assertThat(responseEntity.getStatusCode(), equalTo(ACCEPTED));
    }

    @Test
    public void sendEmailDTOShouldReturnSuccessAcknowledgmentAndConsumeWithAllRetries() throws Exception {
        doThrow(new RuntimeException("from IT")).when(emailClientMock).sendEmail(any());

        ResponseEntity<Void> responseEntity = template.postForEntity(base,
                EmailDTO.builder()
                        .from("from@example.com")
                        .retries(NUMBER_OF_RETRIES)
                        .build(),
                Void.class);

        //allow to consume
        failTopicManualConsumer.commitSync();
        Thread.sleep(delayForConsumer);
        verify(emailClientMock, times(3)).sendEmail(emailCaptor.capture());
        final EmailAvro failedEmail = KafkaTestUtils.getSingleRecord(failTopicManualConsumer, failedTopic).value();
        assertThat(failedEmail.getId(), is(emailCaptor.getValue().getId()));
        assertThat(failedEmail.getRetries(), is(0));

        assertThat(responseEntity.getStatusCode(), equalTo(ACCEPTED));
    }
}
