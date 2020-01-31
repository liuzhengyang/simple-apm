package com.github.liuzhengyang.simpleapm.agent.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.github.liuzhengyang.simpleapm.agent.VertxServer;

public class BannerUtil {
    public static String getBanner() {
        InputStream inputStream = VertxServer.class.getResourceAsStream("/banner.txt");
        if (inputStream != null) {
            String result;
            try (InputStream is = inputStream) {
                ByteArrayOutputStream tmp = new ByteArrayOutputStream();
                byte[] buf = new byte[256];
                while (true) {
                    int len = is.read(buf);
                    if (len == -1) {
                        break;
                    }
                    tmp.write(buf, 0, len);
                }
                result = tmp.toString();
                return result;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "SIMPLE-APM";
    }

}
