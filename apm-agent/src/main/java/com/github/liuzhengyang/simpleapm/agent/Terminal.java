package com.github.liuzhengyang.simpleapm.agent;

import com.github.liuzhengyang.simpleapm.agent.vertx.VertxServer;

public class Terminal {
    public static void write(String output) {
        VertxServer.currentProcess.write(output);
    }
}
