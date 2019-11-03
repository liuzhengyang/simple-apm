package com.github.liuzhengyang.simpleapm.agent.util;

import java.lang.instrument.Instrumentation;
import java.util.HashMap;
import java.util.Map;

import com.github.liuzhengyang.simpleapm.agent.InstrumentationHolder;

public class ClassLoaderUtils {
    public static ClassLoader getLoader(String hashCode) {
        return getAllClassLoader().get(hashCode);
    }

    public static Map<String, ClassLoader> getAllClassLoader() {
        Instrumentation instrumentation = InstrumentationHolder.getInstrumentation();
        Class[] allLoadedClasses = instrumentation.getAllLoadedClasses();
        Map<String, ClassLoader> classLoaderMap = new HashMap<>();
        for (Class clazz : allLoadedClasses) {
            if (clazz.getClassLoader() != null) {
                classLoaderMap.put(Integer.toHexString(clazz.getClassLoader().hashCode()), clazz.getClassLoader());
            }
        }
        return classLoaderMap;
    }
}
