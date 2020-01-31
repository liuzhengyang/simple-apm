package com.github.liuzhengyang.simpleapm.agent;

public class Terminal {
    public static void write(String output) {
        VertxServer.currentProcess.write(output);
    }
}
