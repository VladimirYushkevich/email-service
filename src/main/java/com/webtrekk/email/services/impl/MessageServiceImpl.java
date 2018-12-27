package com.webtrekk.email.services.impl;

import com.webtrekk.email.dto.EmailAvro;
import com.webtrekk.email.messaging.EmailChannels;
import com.webtrekk.email.messaging.EmailEventSource;
import com.webtrekk.email.services.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.webtrekk.email.utils.KafkaUtils.booleanStringFrom;
import static com.webtrekk.email.utils.KafkaUtils.waitUntilStoreIsQueryable;
import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageServiceImpl implements MessageService {

    private final EmailEventSource emailEventSource;
    private final InteractiveQueryService interactiveQueryService;

    @Override
    public String send(EmailAvro message) {
        return emailEventSource.produce(message);
    }

    @Override
    public Map<String, Long> counts() {
        Map<String, Long> counts = new HashMap<>();
        ReadOnlyKeyValueStore<String, Long> store;
        KeyValueIterator<String, Long> keyValueIterator = null;
        try {
            store = waitUntilStoreIsQueryable(interactiveQueryService, EmailChannels.COUNT_MV, QueryableStoreTypes.keyValueStore());
            keyValueIterator = store.all();
            while (keyValueIterator.hasNext()) {
                final KeyValue<String, Long> keyValue = keyValueIterator.next();
                log.info("::keyValue: {}", keyValue);
                counts.put(keyValue.key, keyValue.value);
            }
        } catch (Exception e) {
            log.error("can't iterate in store, reason: ", e);
        } finally {
            if (nonNull(keyValueIterator)) {
                log.debug("::close store iterator");
                keyValueIterator.close();
            }
        }

        return counts;
    }

    @Override
    public String status(String id) {
        ReadOnlyKeyValueStore<String, Long> store;
        long value = 0;
        try {
            store = waitUntilStoreIsQueryable(interactiveQueryService, EmailChannels.STATUS_MV, QueryableStoreTypes.keyValueStore());
            value = store.get(id);
            log.info("::found {}", value);
        } catch (Exception e) {
            log.error("can't find in store by id={}, reason: ", e);
        }
        return booleanStringFrom(value);
    }
}
