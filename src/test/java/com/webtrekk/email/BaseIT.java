package com.webtrekk.email;

import com.webtrekk.email.client.SMTPClient;
import com.webtrekk.email.dto.EmailAvro;
import com.webtrekk.email.serealization.EmailAvroDeserealizer;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.rule.KafkaEmbedded;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

import static java.util.Collections.singleton;
import static org.mockito.Mockito.reset;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
@EnableKafka
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:3333", "port=3333"})
@ActiveProfiles("test")
public abstract class BaseIT {

    @Autowired
    protected SMTPClient emailClientMock;

    @Autowired
    protected TestRestTemplate template;

    @Value(value = "${kafka.test.delayForConsumerInMilliseconds}")
    protected Long delayForConsumer;

    @Autowired
    protected KafkaEmbedded kafkaEmbedded;

    protected Consumer<String, EmailAvro> failTopicManualConsumer;

    protected final String FAILED_TOPIC = "EmailTestFail";

    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @Before
    public void setUp() throws Exception {
        Map<String, Object> consumerProperties = KafkaTestUtils.consumerProps("email.test.fail", "false",
                kafkaEmbedded);
        consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, EmailAvroDeserealizer.class);
        ConsumerFactory<String, EmailAvro> objectObjectDefaultKafkaConsumerFactory = new DefaultKafkaConsumerFactory<>(consumerProperties);
        failTopicManualConsumer = objectObjectDefaultKafkaConsumerFactory.createConsumer();
        failTopicManualConsumer.subscribe(singleton(FAILED_TOPIC));
        failTopicManualConsumer.poll(0);

        for (MessageListenerContainer messageListenerContainer : kafkaListenerEndpointRegistry.getListenerContainers()) {
            ContainerTestUtils.waitForAssignment(messageListenerContainer, kafkaEmbedded.getPartitionsPerTopic());
        }
    }

    @After
    public void tearDown() {
        reset(emailClientMock);
        failTopicManualConsumer.close();
    }
}
