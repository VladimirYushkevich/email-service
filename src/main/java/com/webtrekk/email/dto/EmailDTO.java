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
    @NotNull
    @ApiModelProperty(notes = "Email address from email should be sent")
    private String from;
    @ApiModelProperty(notes = "Subject")
    private String subject;

    public EmailAvro toAvro(Integer retries, String encodedFile) {
        return EmailAvro.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setFrom(from)
                .setSubject(Optional.ofNullable(subject).orElse(""))
                .setRetries(retries)
                .setFile(encodedFile)
                .build();
    }

    public static EmailDTO fromAvro(EmailAvro avro) {
        return EmailDTO.builder()
                .from(avro.getFrom())
                .subject(avro.getSubject())
                .build();
    }
}
