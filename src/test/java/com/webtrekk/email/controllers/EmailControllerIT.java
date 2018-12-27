package com.webtrekk.email.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webtrekk.email.BaseIT;
import com.webtrekk.email.dto.EmailDTO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
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
    private MockMultipartFile multipartFile;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        mockMvc = standaloneSetup(this.emailController).build();
        multipartFile = new MockMultipartFile("file", "test.txt",
                "text/plain", "Spring Framework".getBytes());
    }

    @Test
    public void sendEmailDTOShouldReturnSuccessAcknowledgmentAndConsumeWithoutRetries() throws Exception {
        when(emailClientMock.sendEmail(any(), any())).thenReturn(true);

        MockMultipartFile emailJson = new MockMultipartFile("email", "", APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(EmailDTO.builder()
                        .from("from@example.com")
                        .build()).getBytes());
        final String id = mockMvc.perform(multipart(base)
                .file(emailJson)
                .file(multipartFile))
                .andExpect(status().isAccepted())
                .andReturn()
                .getResponse()
                .getContentAsString();

        //allow to consume
        Thread.sleep(5000);
        assertFalse(id.isEmpty());
        verify(emailClientMock, times(1)).sendEmail(any(), any());
    }

    @Test
    public void sendEmailDTOShouldReturnSuccessAcknowledgmentAndConsumeWithAllRetries() throws Exception {
        when(emailClientMock.sendEmail(any(), any())).thenReturn(false);

        MockMultipartFile emailJson = new MockMultipartFile("email", "", APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(EmailDTO.builder()
                        .from("from@example.com")
                        .build()).getBytes());

        final String id = mockMvc.perform(multipart(base)
                .file(emailJson)
                .file(multipartFile))
                .andExpect(status().isAccepted())
                .andReturn()
                .getResponse()
                .getContentAsString();

        //allow to consume
        Thread.sleep(5000);
        assertFalse(id.isEmpty());
        verify(emailClientMock, times(3)).sendEmail(any(), any());
    }

    @After
    public void tearDown() {
        reset(emailClientMock);
    }
}
