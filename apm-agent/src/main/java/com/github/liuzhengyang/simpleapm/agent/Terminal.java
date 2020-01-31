package com.github.liuzhengyang.simpleapm.agent;

import io.vertx.ext.shell.command.CommandProcess;

public class Terminal {
    private static volatile CommandProcess currentProcess;
    public static void write(String output) {
        getCurrentProcess().write(output);
    }

    public static CommandProcess getCurrentProcess() {
        return currentProcess;
    }

    public static void setCurrentProcess(CommandProcess currentProcess) {
        Terminal.currentProcess = currentProcess;
    }
}
