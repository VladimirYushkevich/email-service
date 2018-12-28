package com.webtrekk.email;

import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import com.webtrekk.email.client.SMTPClient;
import kafka.server.KafkaServer;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

@RunWith(SpringRunner.class)
@SpringBootTest(
        properties = {
                "spring.autoconfigure.exclude=org.springframework.cloud.stream.test.binder.TestSupportBinderAutoConfiguration"
        },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
@Import(ConfigIT.class)
@ActiveProfiles("test")
public abstract class BaseIT {

    @ClassRule
    public static WireMockClassRule wiremock = new WireMockClassRule(options().port(8082));

    @Autowired
    protected SMTPClient emailClientMock;

    @Autowired
    protected TestRestTemplate template;

    @Autowired
    protected InteractiveQueryService interactiveQueryServiceMock;

    @Autowired
    @Qualifier("keyValueStoreCount")
    protected KeyValueStore<Bytes, byte[]> keyValueStoreCount;

    @Autowired
    @Qualifier("keyValueStoreStatus")
    protected KeyValueStore<Bytes, byte[]> keyValueStoreStatus;

    @ClassRule
    public static EmbeddedKafkaRule embeddedKafkaRule = new EmbeddedKafkaRule(1, false);

    private static EmbeddedKafkaBroker embeddedKafka = embeddedKafkaRule.getEmbeddedKafka();

    @BeforeClass
    public static void setup() {
        System.setProperty("spring.kafka.bootstrap-servers", embeddedKafka.getBrokersAsString());
        System.setProperty("spring.cloud.stream.kafka.binder.brokers", "localhost");
    }

    @Before
    public void setUp() throws Exception {
        cleanupStore(keyValueStoreCount);
        cleanupStore(keyValueStoreStatus);
        stub();
    }

    private void cleanupStore(KeyValueStore<Bytes, byte[]> store) {
        final KeyValueIterator<Bytes, byte[]> keyValueIterator = store.all();
        while (keyValueIterator.hasNext()) {
            final KeyValue<Bytes, byte[]> keyValue = keyValueIterator.next();
            store.delete(keyValue.key);
        }
        store.flush();
    }

    private void stub() throws Exception {
        final String schemaJson = readFromInputStream(getClass().getResourceAsStream("/avro/emailavro-schema.json"));
        final String schemaJsonStringified = readFromInputStream(getClass().getResourceAsStream("/avro/emailavro-schema-string.json"));
        wiremock.stubFor(post(urlEqualTo("/subjects/emailavro/versions"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\": 111}")));
        wiremock.stubFor(post(urlEqualTo("/subjects/emailavro"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(schemaJson)));
        wiremock.stubFor(get(urlEqualTo("/subjects/emailavro/versions/2"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/vnd.schemaregistry.v1+json")
                        .withBody(schemaJsonStringified)));
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

    @After
    public void tearDown() {
        embeddedKafka.getKafkaServers().forEach(KafkaServer::shutdown);
        embeddedKafka.getKafkaServers().forEach(KafkaServer::awaitShutdown);
    }
}
