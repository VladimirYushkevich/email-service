package com.webtrekk.email.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webtrekk.email.BaseIT;
import com.webtrekk.email.dto.EmailAvro;
import com.webtrekk.email.dto.EmailDTO;
import com.webtrekk.email.utils.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.multipart.MultipartFile;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

public class EmailControllerIT extends BaseIT {

    @Value("http://localhost:${local.server.port}/api/v1/email")
    private String base;

    @Autowired
    private EmailController emailController;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private ArgumentCaptor<EmailDTO> emailCaptor;
    private ArgumentCaptor<MultipartFile> fileCaptor;
    private MockMultipartFile multipartFile;

    @Override
    @Before
    public void setUp() {
        super.setUp();
        mockMvc = standaloneSetup(this.emailController).build();
        emailCaptor = ArgumentCaptor.forClass(EmailDTO.class);
        fileCaptor = ArgumentCaptor.forClass(MultipartFile.class);
        multipartFile = new MockMultipartFile("file", "test.txt",
                "text/plain", "Spring Framework".getBytes());
    }

    @Test
    public void sendEmailDTOShouldReturnSuccessAcknowledgmentAndConsumeWithoutRetries() throws Exception {
        doNothing().when(emailClientMock).sendEmail(any(), any());

        MockMultipartFile emailJson = new MockMultipartFile("email", "", APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(EmailDTO.builder()
                        .from("from@example.com")
                        .build()).getBytes());
        MvcResult result = mockMvc.perform(multipart(base)
                .file(emailJson)
                .file(multipartFile))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isAccepted())
                .andDo(print());

        //allow to consume
        Thread.sleep(delayForConsumer);
        verify(emailClientMock, times(1)).sendEmail(any(), any());

        assertFalse(result.getResponse().getContentAsString().isEmpty());
    }

    @Test
    public void sendEmailDTOShouldReturnSuccessAcknowledgmentAndConsumeWithAllRetries() throws Exception {
        doThrow(new RuntimeException("from IT")).when(emailClientMock).sendEmail(any(), any());

        MockMultipartFile emailJson = new MockMultipartFile("email", "", APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(EmailDTO.builder()
                        .from("from@example.com")
                        .build()).getBytes());
        MvcResult result = mockMvc.perform(multipart(base)
                .file(emailJson)
                .file(multipartFile))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isAccepted())
                .andDo(print());

        //allow to consume
        failTopicManualConsumer.commitSync();
        Thread.sleep(delayForConsumer);
        verify(emailClientMock, times(3)).sendEmail(emailCaptor.capture(), fileCaptor.capture());
        final EmailAvro failedEmail = KafkaTestUtils.getSingleRecord(failTopicManualConsumer, failedTopic).value();
        assertThat(failedEmail.getFrom(), is(emailCaptor.getValue().getFrom()));
        assertThat(failedEmail.getRetries(), is(0));
        final String encodedFile = failedEmail.getFile();
        assertThat(encodedFile, is(FileUtils.encode(multipartFile)));
        assertArrayEquals(FileUtils.decode(encodedFile).getBytes(), multipartFile.getBytes());

        assertFalse(result.getResponse().getContentAsString().isEmpty());
    }
}
