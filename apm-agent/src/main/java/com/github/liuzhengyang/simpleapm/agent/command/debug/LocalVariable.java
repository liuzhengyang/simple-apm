package com.github.liuzhengyang.simpleapm.agent.command.debug;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * @author liuzhengyang
 * Make something people need.
 * 2020/3/4
 */
public class LocalVariable {
    private String name;
    private String descriptor;
    private int startLine;
    private int endLine;
    /**
     * index in the local variable array of the current frame.
     * If the local variable at index is of type double or long, it
     * occupies both index and index + 1
     */
    private int index;

    public LocalVariable(String name, String descriptor, int startLine, int endLine, int index) {
        this.name = name;
        this.descriptor = descriptor;
        this.startLine = startLine;
        this.endLine = endLine;
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
    }

    public int getStartLine() {
        return startLine;
    }

    public void setStartLine(int startLine) {
        this.startLine = startLine;
    }

    public int getEndLine() {
        return endLine;
    }

    public void setEndLine(int endLine) {
        this.endLine = endLine;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("name", name)
                .append("descriptor", descriptor)
                .append("startLine", startLine)
                .append("endLine", endLine)
                .append("index", index)
                .toString();
    }
}
