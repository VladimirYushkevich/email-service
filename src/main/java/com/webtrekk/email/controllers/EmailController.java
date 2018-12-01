package com.webtrekk.email.controllers;

import com.webtrekk.email.dto.EmailDTO;
import com.webtrekk.email.services.MessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/email")
@AllArgsConstructor
@Slf4j
@Api(description = "REST API with synchronous acknowledgment")
public class EmailController {

    private final MessageService messageService;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.ACCEPTED)
    @ApiOperation(value = "method for sending new Mail")
    public void send(@Valid @RequestBody EmailDTO email) {
        log.info("::sending to message buffer {}", email);

        messageService.produce(email.toAvro());
    }
}
