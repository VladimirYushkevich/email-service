package com.webtrekk.email.controllers;

import com.webtrekk.email.dto.EmailDTO;
import com.webtrekk.email.services.MessageService;
import com.webtrekk.email.utils.FileUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/email")
@RequiredArgsConstructor
@Slf4j
@Api(description = "REST API with synchronous acknowledgment")
public class EmailController {

    private final MessageService messageService;

    @RequestMapping(method = RequestMethod.POST, headers = {"content-type=multipart/mixed", "content-type=multipart/form-data"})
    @ResponseStatus(HttpStatus.ACCEPTED)
    @ApiOperation(value = "method for sending new Mail")
    public String send(@Valid @RequestPart(value = "email") EmailDTO email,
                       @RequestPart(name = "file", required = false) MultipartFile file) {
        log.info("::sending to message buffer {}", email);

        return messageService.send(email.toAvro(FileUtils.encode(file)));
    }

    @RequestMapping(path = "/counts", method = RequestMethod.GET)
    Map<String, Long> counts() {
        log.info("::get counts");
        return messageService.counts();
    }

    @RequestMapping(path = "/status/{id}", method = RequestMethod.GET)
    String status(@PathVariable("id") String id) {
        log.info("::get status by id={}", id);
        return messageService.status(id);
    }
}
