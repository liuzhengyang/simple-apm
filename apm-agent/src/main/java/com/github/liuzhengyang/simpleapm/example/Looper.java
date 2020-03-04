package com.github.liuzhengyang.simpleapm.example;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author liuzhengyang
 * Created on 2019-10-28
 */
public class Looper {

    private static int counter = 0;

    public static void main(String[] args) {
        startLoop();
    }

    public static void startAsync() {
        new Thread(() -> startLoop()).start();
    }

    public static void startLoop() {
        for (int i = 0; i < 1000; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            hello(ThreadLocalRandom.current().nextInt());
        }
    }

    public static String hello(long random) {
        counter++;
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

