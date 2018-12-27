package com.webtrekk.email;

import com.webtrekk.email.client.SMTPClient;
import com.webtrekk.email.messaging.EmailChannels;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.internals.InMemoryKeyValueStore;
import org.mockito.Mockito;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@Slf4j
public class ConfigIT {

    @Bean
    @Primary
    public SMTPClient smtpClient() {
        return Mockito.mock(SMTPClient.class);
    }

    @Bean
    @Primary
    public InteractiveQueryService interactiveQueryService() {
        return Mockito.mock(InteractiveQueryService.class);
    }

    @Bean(name = "materializedCount")
    @Primary
    public Materialized<String, Long, KeyValueStore<Bytes, byte[]>> materializedCount() {
        return Materialized.<String, Long>as(keyValueBytesStoreSupplierCount())
                .withKeySerde(Serdes.String())
                .withValueSerde(Serdes.Long());
    }

    @Bean(name = "materializedStatus")
    @Primary
    public Materialized<String, Long, KeyValueStore<Bytes, byte[]>> materializedStatus() {
        return Materialized.<String, Long>as(keyValueBytesStoreSupplierStatus())
                .withKeySerde(Serdes.String())
                .withValueSerde(Serdes.Long());
    }

    @Bean(name = "keyValueStoreCount")
    @SuppressWarnings("unchecked")
    public KeyValueStore<Bytes, byte[]> keyValueStoreCount() {
        final InMemoryKeyValueStore inMemoryKeyValueStore = new InMemoryKeyValueStore(EmailChannels.COUNT_MV, Serdes.String(),
                Serdes.Long());
        log.info("::keyValueStoreCount {} created in TEST mode", inMemoryKeyValueStore);
        return inMemoryKeyValueStore;
    }

    @Bean(name = "keyValueStoreStatus")
    @SuppressWarnings("unchecked")
    public KeyValueStore<Bytes, byte[]> keyValueStoreStatus() {
        final InMemoryKeyValueStore inMemoryKeyValueStore = new InMemoryKeyValueStore(EmailChannels.STATUS_MV, Serdes.String(),
                Serdes.Long());
        log.info("::keyValueStoreStatus {} created in TEST mode", inMemoryKeyValueStore);
        return inMemoryKeyValueStore;
    }

    private KeyValueBytesStoreSupplier keyValueBytesStoreSupplierCount() {
        return new KeyValueBytesStoreSupplier() {
            public String name() {
                return EmailChannels.COUNT_MV;
            }

            public KeyValueStore<Bytes, byte[]> get() {
                return keyValueStoreCount();
            }

            public String metricsScope() {
                return "in-memory-state-count-test";
            }
        };
    }

    private KeyValueBytesStoreSupplier keyValueBytesStoreSupplierStatus() {
        return new KeyValueBytesStoreSupplier() {
            public String name() {
                return EmailChannels.STATUS_MV;
            }

            public KeyValueStore<Bytes, byte[]> get() {
                return keyValueStoreStatus();
            }

            public String metricsScope() {
                return "in-memory-state-status-test";
            }
        };
    }
}
