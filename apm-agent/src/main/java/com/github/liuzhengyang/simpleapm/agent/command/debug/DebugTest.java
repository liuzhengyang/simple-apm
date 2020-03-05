package com.github.liuzhengyang.simpleapm.agent.command.debug;

import java.util.Random;

/**
 * @author liuzhengyang
 * Make something people need.
 * 2020/3/4
 */
public class DebugTest {
    private String name;
    private static String tip;
    private int count;

    public void test() {
//        int a = count;
        System.out.println(count++);
        System.out.println(tip);
        int a = 1;
        int b = 2;
//        DebugUtils.printField("hello", name);
        name = String.valueOf(System.currentTimeMillis());
        try {
            Thread.sleep(100);
            double c = b + Math.random();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

    }
}
