package com.github.liuzhengyang.simpleapm.agent.command.debug;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public static void printField(String field, Object value) {
        logger.info("Field {} value {}", field, value);
    }
}
