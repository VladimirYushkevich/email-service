package com.webtrekk.email.services.impl;

import com.webtrekk.email.BaseIT;
import com.webtrekk.email.dto.EmailAvro;
import com.webtrekk.email.services.MessageService;
import org.apache.kafka.common.utils.Bytes;
import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.ByteBuffer;

import static com.webtrekk.email.TestUtils.getEmailAvroEvent;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class MessageServiceImplIT extends BaseIT {

    @Autowired
    private MessageService messageService;

    @Test
    public void shouldSendEmailWithoutRetries() throws Exception {
        when(emailClientMock.sendEmail(any(), any())).thenReturn(true);

        final EmailAvro emailAvro = getEmailAvroEvent();
        final String messageId = messageService.send(emailAvro);

        //allow to consume
        Thread.sleep(5000);
        assertThat(messageId, is(emailAvro.getId()));
        assertThat(keyValueStoreStatus.get(stringToBytes(messageId)), is(longToByteArray(1L)));
        verify(emailClientMock, times(1)).sendEmail(any(), any());
    }

    @Test
    public void shouldUseAllRetriesWhenEmailClientUnsuccessfullySendEmail() throws Exception {
        when(emailClientMock.sendEmail(any(), any())).thenReturn(false);

        final EmailAvro emailAvro = getEmailAvroEvent();
        final String messageId = messageService.send(emailAvro);

        //allow to consume
        Thread.sleep(5000);
        assertThat(messageId, is(emailAvro.getId()));
        assertThat(keyValueStoreStatus.get(stringToBytes(messageId)), is(longToByteArray(0)));
        verify(emailClientMock, times(3)).sendEmail(any(), any());
    }

    @Test
    public void shouldSendEmailWithRecoveringAfterRetry() throws Exception {
        when(emailClientMock.sendEmail(any(), any())).thenReturn(false, true);

        final EmailAvro emailAvro = getEmailAvroEvent();
        final String messageId = messageService.send(emailAvro);

        //allow to consume
        Thread.sleep(5000);
        assertThat(messageId, is(emailAvro.getId()));
        assertThat(keyValueStoreStatus.get(stringToBytes(messageId)), is(longToByteArray(1L)));
        verify(emailClientMock, times(2)).sendEmail(any(), any());
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

    public static long bytesToLong(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();//need flip
        return buffer.getLong();
    }
}
