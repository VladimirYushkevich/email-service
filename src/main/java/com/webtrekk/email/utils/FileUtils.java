package com.webtrekk.email.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FileUtils {

    public static String encode(MultipartFile file) {
        try {
            return Base64.encodeBase64String(file.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static MultipartFile decode(String encodedFile) {
        final byte[] bytes = Base64.decodeBase64(encodedFile);

        return new BASE64DecodedMultipartFile(bytes);
    }
}
