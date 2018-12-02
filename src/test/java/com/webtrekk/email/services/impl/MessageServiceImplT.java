package com.webtrekk.email.services.impl;

import com.webtrekk.email.BaseIT;
import com.webtrekk.email.dto.EmailAvro;
import com.webtrekk.email.services.MessageService;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class MessageServiceImplT extends BaseIT {

    @Autowired
    private MessageService messageService;

    private static final String INPUT_TOPIC = "testEmbeddedIn";
    private static final String OUTPUT_TOPIC = "testEmbeddedOut";
    private static final String GROUP_NAME = "embeddedKafkaApplicationTest";

    @Test
    public void testSendReceive() {
        Map<String, Object> senderProps = KafkaTestUtils.producerProps(embeddedKafkaRule.getEmbeddedKafka());
        senderProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class);
        senderProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class);
        DefaultKafkaProducerFactory<byte[], byte[]> pf = new DefaultKafkaProducerFactory<>(senderProps);
        KafkaTemplate<byte[], byte[]> template = new KafkaTemplate<>(pf, true);
        template.setDefaultTopic(INPUT_TOPIC);
        template.sendDefault("foo".getBytes());

        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(GROUP_NAME, "false", embeddedKafkaRule.getEmbeddedKafka());
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class);
        DefaultKafkaConsumerFactory<byte[], byte[]> cf = new DefaultKafkaConsumerFactory<>(consumerProps);

        Consumer<byte[], byte[]> consumer = cf.createConsumer();
        consumer.subscribe(Collections.singleton(OUTPUT_TOPIC));
        ConsumerRecords<byte[], byte[]> records = consumer.poll(Duration.ofSeconds(10));
        consumer.commitSync();

        assertThat(records.count()).isEqualTo(1);
        assertThat(new String(records.iterator().next().value())).isEqualTo("FOO");
    }

    @Test
    public void produce() throws Exception {
        doNothing().when(emailClientMock).sendEmail(any());

        messageService.produce(getEmailAvroEvent());

        //allow to consume
        Thread.sleep(10000);
        verify(emailClientMock, times(1)).sendEmail(any());
    }

    private EmailAvro getEmailAvroEvent() {
        return EmailAvro.newBuilder()
                .setFrom("from")
                .setId("id")
                .setRetries(3)
                .setSubject("subject")
                .build();
    }
}
