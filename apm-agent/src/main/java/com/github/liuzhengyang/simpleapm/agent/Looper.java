package com.github.liuzhengyang.simpleapm.agent;

import static jdk.nashorn.internal.objects.NativeMath.random;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author liuzhengyang
 * Created on 2019-10-28
 */
public class Looper {
    public static void main(String[] args) {
        asyncLoop();
    }
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
        random(random);
        return String.valueOf(System.currentTimeMillis());
    }

    public static Result random(long input) {
        return new Result().setTime(System.currentTimeMillis());
    }

    public static class Result {
        long time;
        String msg;

        public long getTime() {
            return time;
        }

        public Result setTime(long time) {
            this.time = time;
            return this;
        }

        public String getMsg() {
            return msg;
        }

        public Result setMsg(String msg) {
            this.msg = msg;
            return this;
        }
    }
}

