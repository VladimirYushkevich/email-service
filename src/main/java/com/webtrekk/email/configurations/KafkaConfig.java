package com.webtrekk.email.configurations;

import com.webtrekk.email.messaging.EmailChannels;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.schema.client.ConfluentSchemaRegistryClient;
import org.springframework.cloud.stream.schema.client.SchemaRegistryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class KafkaConfig {

    @Bean
    public SchemaRegistryClient schemaRegistryClient(@Value("${spring.cloud.stream.schema-registry-client.endpoint}") final String endpoint) {
        ConfluentSchemaRegistryClient client = new ConfluentSchemaRegistryClient();
        client.setEndpoint(endpoint);
        return client;
    }

    @Bean(name = "materializedCount")
    @Profile("!test")
    public Materialized materializedCount() {
        return Materialized.<String, Long, KeyValueStore<Bytes, byte[]>>as(EmailChannels.COUNT_MV)
                .withKeySerde(Serdes.String())
                .withValueSerde(Serdes.Long());
    }

    @Bean(name = "materializedStatus")
    @Profile("!test")
    public Materialized materializedStatus() {
        return Materialized.<String, Long, KeyValueStore<Bytes, byte[]>>as(EmailChannels.STATUS_MV)
                .withKeySerde(Serdes.String())
                .withValueSerde(Serdes.Long());
    }
}
