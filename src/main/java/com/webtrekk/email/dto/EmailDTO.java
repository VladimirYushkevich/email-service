package com.webtrekk.email.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailDTO {
    @ApiModelProperty(notes = "Unique identifier for tracking a message")
    private String id;
    @NotNull
    @ApiModelProperty(notes = "Email address from email should be sent")
    private String from;
    @ApiModelProperty(notes = "Subject")
    private String subject;
    @NotNull
    @ApiModelProperty(notes = "Number of retry attempts")
    private Integer retries;

    public EmailAvro toAvro() {
        return EmailAvro.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setFrom(from)
                .setSubject(Optional.ofNullable(subject).orElse(""))
                .setRetries(retries)
                .build();
    }

    public static EmailDTO fromAvro(EmailAvro avro) {
        return EmailDTO.builder()
                .id(avro.getId())
                .from(avro.getFrom())
                .subject(avro.getSubject())
                .retries(avro.getRetries())
                .build();
    }
}
