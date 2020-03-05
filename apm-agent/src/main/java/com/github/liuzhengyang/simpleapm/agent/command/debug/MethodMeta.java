package com.github.liuzhengyang.simpleapm.agent.command.debug;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * @author liuzhengyang
 * Make something people need.
 * 2020/3/4
 */
public class MethodMeta {
    private String name;
    private String descriptor;
    private List<LocalVariable> localVariableTable = new ArrayList<>();

    public MethodMeta() {
    }

    public MethodMeta(String name, String descriptor, List<LocalVariable> localVariableTable) {
        this.name = name;
        this.descriptor = descriptor;
        this.localVariableTable = localVariableTable;
    }

    public void addLocalVariable(LocalVariable localVariable) {
        localVariableTable.add(localVariable);
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

    public List<LocalVariable> getLocalVariableTable() {
        return localVariableTable;
    }

    public void setLocalVariableTable(List<LocalVariable> localVariableTable) {
        this.localVariableTable = localVariableTable;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("name", name)
                .append("descriptor", descriptor)
                .append("localVariableTable", localVariableTable)
                .toString();
    }
}
