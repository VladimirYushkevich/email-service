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
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Duration;
import java.util.Map;

import static java.util.Collections.singleton;
import static org.mockito.Mockito.reset;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
@EnableKafka
@EmbeddedKafka
@ActiveProfiles("test")
public abstract class BaseIT {

    @Autowired
    protected SMTPClient emailClientMock;

    @Value(value = "${kafka.test.delayForConsumerInMilliseconds}")
    protected Long delayForConsumer;

    @Value(value = "${kafka.email.fail.topic.name}")
    protected String failedTopic;

    protected Consumer<String, EmailAvro> failTopicManualConsumer;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Before
    public void setUp() {
        Map<String, Object> consumerProperties = KafkaTestUtils.consumerProps("email.test.fail", "false",
                embeddedKafkaBroker);
        consumerProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, EmailAvroDeserealizer.class);
        ConsumerFactory<String, EmailAvro> manualConsumerFactory = new DefaultKafkaConsumerFactory<>(consumerProperties);
        failTopicManualConsumer = manualConsumerFactory.createConsumer();
        failTopicManualConsumer.subscribe(singleton(failedTopic));
        failTopicManualConsumer.poll(Duration.ZERO);
    }

    @After
    public void tearDown() {
        reset(emailClientMock);
        failTopicManualConsumer.close();
    }
}
