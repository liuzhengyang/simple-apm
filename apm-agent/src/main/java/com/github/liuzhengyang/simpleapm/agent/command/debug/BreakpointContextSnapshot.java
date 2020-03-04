package com.github.liuzhengyang.simpleapm.agent.command.debug;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author liuzhengyang
 * Make something people need.
 * 2020/3/3
 */
public class BreakpointContextSnapshot {
    private Map<String, Object> instanceFieldValues;
    private Map<String, Object> staticFieldValues;
    private Map<String, Object> localVariableValues;
    private List<String> stackTrace;

    public BreakpointContextSnapshot() {
        this.instanceFieldValues = new HashMap<>();
        this.staticFieldValues = new HashMap<>();
        this.localVariableValues = new HashMap<>();
        this.stackTrace = new ArrayList<>();
    }

    public void addInstanceField(String name, Object value) {
        instanceFieldValues.put(name, value);
    }

    public void addStaticField(String name, Object value) {
        staticFieldValues.put(name, value);
    }

    public void addLocalVariable(String name, Object value) {
        localVariableValues.put(name, value);
    }

    public void setStackTrace(List<String> stackTrace) {
        this.stackTrace.addAll(stackTrace);
    }

}
