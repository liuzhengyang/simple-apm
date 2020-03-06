package com.github.liuzhengyang.simpleapm.agent.command.debug;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.liuzhengyang.simpleapm.agent.util.JsonUtils;
import com.github.liuzhengyang.simpleapm.agent.web.WebServer;

/**
 * @author liuzhengyang
 * Make something people need.
 * 2020/3/4
 */
public class DebugUtils {
    private static final Logger logger = LoggerFactory.getLogger(DebugUtils.class);

    public static void hasBreakpoint(String className, int lineNumber) {
        if (BreakpointRegistry.hasBreakpoint(className, lineNumber)) {
            System.out.println("");
        }
    }

    public static void printStaticField(String field, Object value) {
        logger.info("Static field {} value {}", field, JsonUtils.toJson(value));
        WebServer.send(String.format("Static field %s value %s", field, JsonUtils.toJson(value)));
    }

    public static void printInstanceField(String field, Object value) {
        logger.info("Instance field {} value {}", field, JsonUtils.toJson(value));
        WebServer.send(String.format("Instance field %s value %s", field, JsonUtils.toJson(value)));
    }

    public static void printLocalVariable(String variableName, Object value) {
        logger.info("LocalVariable {} value {}", variableName, JsonUtils.toJson(value));
        WebServer.send(String.format("LocalVariable %s value %s", variableName, JsonUtils.toJson(value)));
    }

    public static void printStackTrace(StackTraceElement[] stackTraces) {
        logger.info("Stack trace {}", Arrays.toString(stackTraces));
        WebServer.send("StackTrace");
        for (StackTraceElement stackTrace : stackTraces) {
            WebServer.send(stackTrace.toString());
        }
    }

}
