package com.github.liuzhengyang.simpleapm.agent.command.debug;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Throwables;

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
        logger.info("Static field {} value {}", field, value);
    }

    public static void printInstanceField(String field, Object value) {
        logger.info("Instance field {} value {}", field, value);
    }

    public static void printLocalVariable(String variableName, Object value) {
        logger.info("LocalVariable {} value {}", variableName, value);
    }

    public static void printStackTrace(StackTraceElement[] stackTraces) {
        logger.info("Stack trace {}", Arrays.toString(stackTraces));
    }

    public static void main(String[] args) {
        int a = 1;
        printLocalVariable("", a);
        printStackTrace(new Throwable().getStackTrace());
    }
}
