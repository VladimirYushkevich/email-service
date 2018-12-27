package com.webtrekk.email;

import com.webtrekk.email.utils.FileUtils;
import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.Assert.assertArrayEquals;

public class FileUtilsTest {

    @Test
    public void testEncodingDecoding() throws Exception {
        MockMultipartFile multipartFile = new MockMultipartFile("file", "test.txt",
                "text/plain", "Spring Framework".getBytes());

        String encodedString = FileUtils.encode(multipartFile);
        final MultipartFile decodedFile = FileUtils.decode(encodedString);

        assertArrayEquals(decodedFile.getBytes(), multipartFile.getBytes());

    }
}
