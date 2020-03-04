package com.github.liuzhengyang.simpleapm.agent.command.debug;

/**
 * @author liuzhengyang
 * Make something people need.
 * 2020/3/3
 */
public class Breakpoint {
    /**
     * className a.b.c
     */
    private String className;
    /**
     * line number in source code
     */
    private int lineNumber;
    /**
     * breakpoint stop condition
     */
    private Condition condition;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }
}
