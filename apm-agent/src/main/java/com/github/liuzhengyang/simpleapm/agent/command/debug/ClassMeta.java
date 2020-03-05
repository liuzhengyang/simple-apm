package com.github.liuzhengyang.simpleapm.agent.command.debug;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

import javassist.bytecode.FieldInfo;

/**
 * @author liuzhengyang
 * Make something people need.
 * 2020/3/4
 */
public class ClassMeta {
    private List<FieldInfo> fields;
    private List<MethodMeta> methods;

    public ClassMeta() {
        this.fields = new ArrayList<>();
        this.methods = new ArrayList<>();
    }

    public void addField(FieldInfo fieldInfo) {
        this.fields.add(fieldInfo);
    }

    public void addMethod(MethodMeta methodMeta) {
        this.methods.add(methodMeta);
    }

    public MethodMeta getMethod(String methodName, String descriptor) {
        for (MethodMeta methodMeta : methods) {
            if (methodMeta.getName().equals(methodName) && methodMeta.getDescriptor().equals(descriptor)) {
                return methodMeta;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("fields", fields)
                .append("methods", methods)
                .toString();
    }
}
