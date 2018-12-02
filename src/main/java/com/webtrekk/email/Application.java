package com.webtrekk.email;

import com.webtrekk.email.dto.EmailAvro;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.schema.avro.AvroSchemaMessageConverter;
import org.springframework.cloud.stream.schema.client.ConfluentSchemaRegistryClient;
import org.springframework.cloud.stream.schema.client.SchemaRegistryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.converter.MessageConverter;

//import org.springframework.cloud.stream.schema.avro.AvroSchemaMessageConverter;


@SpringBootApplication
public class Application {

//    public static class EmailEventSource implements ApplicationRunner {
//
//        private final MessageChannel messageChannel;
//
//        public EmailEventSource(EmailChannels emailBinding) {
//            this.messageChannel = emailBinding.emailsOut();
//        }
//
//        @Override
//        public void run(ApplicationArguments args) throws Exception {
//        }
//    }

    @Bean
    public SchemaRegistryClient schemaRegistryClient(@Value("${spring.cloud.stream.schemaRegistryClient.endpoint}") final String endpoint) {
        ConfluentSchemaRegistryClient client = new ConfluentSchemaRegistryClient();
        client.setEndpoint(endpoint);
        return client;
    }

    @Bean
    public MessageConverter emailMessageConverter() {
        AvroSchemaMessageConverter converter = new AvroSchemaMessageConverter();
        converter.setSchema(EmailAvro.SCHEMA$);
        return converter;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
