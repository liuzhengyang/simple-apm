package com.github.liuzhengyang.simpleapm.agent.command.debug;

import java.util.HashMap;
import java.util.Map;

/**
 * @author liuzhengyang
 * Make something people need.
 * 2020/3/3
 */
public class BreakpointRegistry {

    private static Map<String, Map<Integer, Breakpoint>> classNameToBreakpointsMap = new HashMap<>();

    public static void addBreakpoint(String className, int lineNumber, Breakpoint breakpoint) {
        classNameToBreakpointsMap.computeIfAbsent(className, c -> new HashMap<>())
                .put(lineNumber, breakpoint);
    }

    public static boolean hasBreakpoint(String className, int lineNumber) {
        Map<Integer, Breakpoint> lineToBreakPointMap = classNameToBreakpointsMap.get(className);
        return lineToBreakPointMap != null && lineToBreakPointMap.get(lineNumber) != null;
    }
}
