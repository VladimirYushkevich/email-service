package com.webtrekk.email.controllers;

import com.webtrekk.email.dto.EmailDTO;
import com.webtrekk.email.services.MessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/email")
@RequiredArgsConstructor
@Slf4j
@Api(description = "REST API with synchronous acknowledgment")
public class EmailController {

    private final MessageService messageService;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.ACCEPTED)
    @ApiOperation(value = "method for sending new Mail")
    public String send(@Valid @RequestBody EmailDTO email) {
        log.info("::sending to message buffer {}", email);
        return messageService.send(email.toAvro());
    }

    @RequestMapping(path = "/counts", method = RequestMethod.GET)
    Map<String, Long> counts() {
        log.info("::get counts");
        return messageService.stats();
    }

    @RequestMapping(path = "/status/{id}", method = RequestMethod.GET)
    String counts(@PathVariable("id") String id) {
        log.info("::get status by id={}", id);
        return messageService.status(id);
    }
}
