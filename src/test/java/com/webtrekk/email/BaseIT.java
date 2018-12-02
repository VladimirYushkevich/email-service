package com.webtrekk.email;

import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import com.webtrekk.email.client.SMTPClient;
import com.webtrekk.email.messaging.EmailChannels;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.mockito.Mockito.reset;

@RunWith(SpringRunner.class)
@SpringBootTest(
        properties = {
                "spring.autoconfigure.exclude=org.springframework.cloud.stream.test.binder.TestSupportBinderAutoConfiguration"
        },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@DirtiesContext
//@EnableKafka
//@EmbeddedKafka
@ActiveProfiles("test")
public abstract class BaseIT {

    @ClassRule
    public static WireMockClassRule wiremock = new WireMockClassRule(options().port(8990));

    @Autowired
    protected SMTPClient emailClientMock;

    @Autowired
    protected TestRestTemplate template;

//    @Value(value = "${kafka.email.fail.topic.name}")
//    protected String failedTopic;

//    @Autowired
//    protected TestSupportBinder testSupportBinder;

    @Autowired
    private EmailChannels emailChannels;

//    protected Consumer<String, EmailAvro> failTopicManualConsumer;
//
//    @Autowired
//    protected EmbeddedKafkaBroker embeddedKafkaBroker;

    @ClassRule
    public static EmbeddedKafkaRule embeddedKafkaRule = new EmbeddedKafkaRule(1, true);

    @BeforeClass
    public static void setup() {
        System.setProperty("spring.kafka.bootstrap-servers", embeddedKafkaRule.getEmbeddedKafka().getBrokersAsString());
    }

    @Before
    public void setUp() throws Exception {
//        testSupportBinder.messageCollector().forChannel(emailChannels.inboundEmails()).clear();
        stub();
//        Map<String, Object> consumerProperties = KafkaTestUtils.consumerProps("email.test.fail", "false",
//                embeddedKafkaBroker);
//        consumerProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
//        consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, EmailAvroDeserealizer.class);
//        ConsumerFactory<String, EmailAvro> manualConsumerFactory = new DefaultKafkaConsumerFactory<>(consumerProperties);
//        failTopicManualConsumer = manualConsumerFactory.createConsumer();
//        failTopicManualConsumer.subscribe(singleton(failedTopic));
//        failTopicManualConsumer.poll(Duration.ZERO);
    }

    private void stub() throws Exception {
        final String schemaJson = readFromInputStream(getClass().getResourceAsStream("/avro/emailavro-schema.json"));
        wiremock.stubFor(post(urlEqualTo("/subjects/emailavro/versions"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\":111}")));
        wiremock.stubFor(post(urlEqualTo("/subjects/emailavro"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(schemaJson)));
        wiremock.stubFor(get(urlEqualTo("/subjects/emailavro/versions/2"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(schemaJson)));
    }

    @After
    public void tearDown() {
        reset(emailClientMock);
//        failTopicManualConsumer.close();
    }

    private String readFromInputStream(InputStream inputStream) throws IOException {
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        }
        return resultStringBuilder.toString();
    }
}
