package com.github.liuzhengyang.simpleapm.agent.util;

import com.github.liuzhengyang.simpleapm.agent.Constants;

import io.vertx.ext.shell.command.CommandProcess;

/**
 * @author liuzhengyang
 */
public class CommandProcessUtil {
    public static void println(CommandProcess process, String format, Object... args) {
        process.write(String.format(format, args) + Constants.CRLF);
    }
}
