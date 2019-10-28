package com.github.liuzhengyang.simpleapm.agent;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author liuzhengyang
 * Created on 2019-10-28
 */
public class Looper {
    public static void asyncLoop() {
        new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                hello(ThreadLocalRandom.current().nextInt());
            }
        }).start();
    }

    public static String hello(long random) {
        System.out.println("Hello");
        return String.valueOf(System.currentTimeMillis());
    }
}
