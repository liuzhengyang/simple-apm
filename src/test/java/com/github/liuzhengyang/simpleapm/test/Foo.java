package com.github.liuzhengyang.simpleapm.test;

import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.Test;

/**
 * @author liuzhengyang
 * Created on 2019-10-28
 */
public class Foo {
    @Test
    public void test() throws InterruptedException {
        for (int i = 0; i < 10000; i++) {
            Thread.sleep(1000);
            hello(ThreadLocalRandom.current().nextInt());
        }
    }

    static String hello(int a) {
        System.out.println("Running " + a);
        return String.valueOf(System.currentTimeMillis());
    }
}
