package com.github.liuzhengyang.simpleapm.agent.util;

/**
 * @author liuzhengyang
 * Make something people want.
 * 2020/2/29
 */
public class TypeUtil {
    public static String getInternalName(String className) {
        return className.replace(".", "/");
    }
}
