package com.ecfront.dew.auth.helper;

import com.ecfront.dew.common.$;
import com.github.cage.GCage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CaptchaHelper {

    public static File generate(String text) throws IOException {
        File temp = File.createTempFile("dew_captcha_", ".jpg");
        try (FileOutputStream os = new FileOutputStream(temp)) {
            temp.deleteOnExit();
            new GCage().draw(text, os);
            return temp;
        }
    }

    public static String generateToBase64(String text) throws IOException {
        byte[] bytes = new GCage().draw(text);
        return $.encrypt.encodeBytesToBase64(bytes, "UTF-8");
    }


}
