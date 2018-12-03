package com.webtrekk.email.controllers;

import com.webtrekk.email.dto.EmailAvro;
import com.webtrekk.email.dto.EmailDTO;
import com.webtrekk.email.services.MessageService;
import com.webtrekk.email.utils.FileUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/email")
@RequiredArgsConstructor
@Slf4j
@Api(description = "REST API with synchronous acknowledgment")
public class EmailController {

    private final MessageService messageService;

    @Value(value = "${kafka.email.retry.cnt}")
    private Integer retries;

    @RequestMapping(method = RequestMethod.POST, headers = {"content-type=multipart/mixed", "content-type=multipart/form-data"})
    @ResponseStatus(HttpStatus.ACCEPTED)
    @ApiOperation(value = "method for sending new Mail")
    public DeferredResult<String> send(@Valid @RequestPart(value = "email") EmailDTO email,
                                       @RequestPart(name = "file", required = false) MultipartFile file) {
        log.info("::sending to message buffer {}", email);


        final DeferredResult<String> deferredResult = new DeferredResult<>();
        final ListenableFuture<SendResult<String, EmailAvro>> produceFuture = messageService.produce(email.toAvro(retries,
                FileUtils.encode(file)));

        produceFuture.addCallback(new ListenableFutureCallback<SendResult<String, EmailAvro>>() {
            @Override
            public void onSuccess(SendResult<String, EmailAvro> result) {
                deferredResult.setResult(result.getProducerRecord().value().getId());
            }

            @Override
            public void onFailure(Throwable ex) {
                deferredResult.setErrorResult(ex.getMessage());
            }
        });

        return deferredResult;
    }
}
