package com.webtrekk.email.messaging;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface EmailChannels {

    String EMAIL_OUTPUT = "emails-out";
    String EMAIL_INPUT = "emails-in";

    @Output(EMAIL_OUTPUT)
    MessageChannel outboundEmails();

    @Input(EMAIL_INPUT)
    SubscribableChannel inboundEmails();
}
