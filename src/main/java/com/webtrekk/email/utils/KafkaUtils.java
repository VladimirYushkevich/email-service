package com.webtrekk.email.utils;

import com.webtrekk.email.dto.EmailAvro;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.errors.InvalidStateStoreException;
import org.apache.kafka.streams.state.QueryableStoreType;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public final class KafkaUtils {

    public static Message<EmailAvro> buildMessage(EmailAvro message) {
        return MessageBuilder
                .withPayload(message)
                .setHeader(KafkaHeaders.MESSAGE_KEY, message.getId().getBytes())
                .build();
    }

    public static String booleanStringFrom(Long value) {
        return String.valueOf(value == 1);
    }

    public static Long longFrom(Boolean success) {
        return success ? 1L : 0;
    }

    public static <T> T waitUntilStoreIsQueryable(InteractiveQueryService interactiveQueryService, String storeName,
                                                  QueryableStoreType<T> queryableStoreType) throws InterruptedException {
        while (true) {
            try {
                return interactiveQueryService.getQueryableStore(storeName, queryableStoreType);
            } catch (InvalidStateStoreException ignored) {
                log.info("store not yet ready for querying. Retry.....");
                Thread.sleep(100);
            }
        }
    }
}
