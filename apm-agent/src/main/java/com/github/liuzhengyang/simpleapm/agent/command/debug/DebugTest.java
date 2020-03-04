package com.github.liuzhengyang.simpleapm.agent.command.debug;

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
//        DebugUtils.printField("hello", name);
        name = String.valueOf(System.currentTimeMillis());
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

    }
}
