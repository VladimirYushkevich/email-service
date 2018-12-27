package com.webtrekk.email.services.impl;

import com.webtrekk.email.BaseIT;
import com.webtrekk.email.dto.EmailAvro;
import com.webtrekk.email.services.MessageService;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.state.QueryableStoreType;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.ByteBuffer;

import static com.webtrekk.email.TestUtils.getEmailAvroEvent;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MessageServiceImplIT extends BaseIT {

    @Autowired
    private MessageService messageService;

    @Override
    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        super.setUp();

        Mockito.when(interactiveQueryServiceMock.getQueryableStore(any(), any(QueryableStoreType.class))).thenReturn(keyValueStoreStatus);
//        Mockito.when(interactiveQueryServiceMock.getQueryableStore(any(), any(QueryableStoreType.class))).thenReturn(keyValueStoreCount);
    }

    @Test
    public void shouldSendEmailWithoutRetries() throws Exception {
        when(emailClientMock.sendEmail(any())).thenReturn(true);

        final EmailAvro emailAvro = getEmailAvroEvent();
        final String messageId = messageService.send(emailAvro);

        //allow to consume
        Thread.sleep(5000);
        assertThat(messageId, is(emailAvro.getId()));
        //it is not possible to clean kafka topic within one ClassRule. For more control something like TopologyTestDriver
        //should be taken into account.
//        assertThat(messageService.stats().get(Bytes.wrap("true".getBytes())), is(longToByteArray(2L)));
        assertThat(keyValueStoreStatus.get(stringToBytes(messageId)), is(longToByteArray(1L)));
        verify(emailClientMock, times(1)).sendEmail(any());
    }

    @Test
    public void shouldUseAllRetriesWhenEmailClientUnsuccessfullySendEmail() throws Exception {
        when(emailClientMock.sendEmail(any())).thenReturn(false);

        final String messageId = messageService.send(getEmailAvroEvent());

        //allow to consume
        Thread.sleep(5000);
        assertThat(messageId, is("id"));
        assertThat(messageService.stats().get(Bytes.wrap("false".getBytes())), is(longToByteArray(1L)));
        verify(emailClientMock, times(3)).sendEmail(any());
    }

    @Test
    public void shouldSendEmailWithRecoveringAfterRetry() throws Exception {
        when(emailClientMock.sendEmail(any())).thenReturn(false, true);

        final String messageId = messageService.send(getEmailAvroEvent());

        //allow to consume
        Thread.sleep(5000);
        assertThat(messageId, is("id"));
        assertThat(messageService.stats().get(Bytes.wrap("true".getBytes())), is(longToByteArray(1L)));
        verify(emailClientMock, times(2)).sendEmail(any());
    }

    @After
    public void tearDown() {
        reset(emailClientMock);
    }

    private byte[] longToByteArray(long value) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(0, value);
        return buffer.array();
    }

    private Bytes stringToBytes(String value) {
        return Bytes.wrap(value.getBytes());
    }
}
