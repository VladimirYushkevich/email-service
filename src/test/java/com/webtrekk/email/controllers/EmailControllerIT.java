package com.webtrekk.email.controllers;

public class EmailControllerIT {

//    @Value("http://localhost:${local.server.port}/api/v1/email")
//    private String base;
//
//    private final int NUMBER_OF_RETRIES = 2;
//
//    private ArgumentCaptor<EmailDTO> emailCaptor = ArgumentCaptor.forClass(EmailDTO.class);
//
//    @Test
//    public void sendEmailDTOShouldReturnSuccessAcknowledgmentAndConsumeWithoutRetries() throws Exception {
//        doNothing().when(emailClientMock).sendEmail(any());
//
//        ResponseEntity<Void> responseEntity = template.postForEntity(base,
//                EmailDTO.builder()
//                        .from("from@example.com")
//                        .retries(NUMBER_OF_RETRIES)
//                        .build(),
//                Void.class);
//
//        //allow to consume
//        Thread.sleep(delayForConsumer);
//        verify(emailClientMock, times(1)).sendEmail(any());
//
//        assertThat(responseEntity.getStatusCode(), equalTo(ACCEPTED));
//    }
//
//    @Test
//    public void sendEmailDTOShouldReturnSuccessAcknowledgmentAndConsumeWithAllRetries() throws Exception {
//        doThrow(new RuntimeException("from IT")).when(emailClientMock).sendEmail(any());
//
//        ResponseEntity<Void> responseEntity = template.postForEntity(base,
//                EmailDTO.builder()
//                        .from("from@example.com")
//                        .retries(NUMBER_OF_RETRIES)
//                        .build(),
//                Void.class);
//
//        //allow to consume
//        failTopicManualConsumer.commitSync();
//        Thread.sleep(delayForConsumer);
//        verify(emailClientMock, times(3)).sendEmail(emailCaptor.capture());
//        final EmailAvro failedEmail = KafkaTestUtils.getSingleRecord(failTopicManualConsumer, failedTopic).value();
//        assertThat(failedEmail.getId(), is(emailCaptor.getValue().getId()));
//        assertThat(failedEmail.getRetries(), is(0));
//
//        assertThat(responseEntity.getStatusCode(), equalTo(ACCEPTED));
//    }
}
