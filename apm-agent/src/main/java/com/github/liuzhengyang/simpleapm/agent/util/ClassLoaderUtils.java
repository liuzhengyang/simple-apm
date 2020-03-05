package com.github.liuzhengyang.simpleapm.agent.util;

import java.lang.instrument.Instrumentation;
import java.util.HashMap;
import java.util.Map;

import com.github.liuzhengyang.simpleapm.agent.InstrumentationHolder;

public class ClassLoaderUtils {
    public static ClassLoader getLoader(String hashCode) {
        return getAllClassLoader().getOrDefault(hashCode, ClassLoaderUtils.class.getClassLoader());
    }

    public static Map<String, ClassLoader> getAllClassLoader() {
        Instrumentation instrumentation = InstrumentationHolder.getInstrumentation();
        Class[] allLoadedClasses = instrumentation.getAllLoadedClasses();
        Map<String, ClassLoader> classLoaderMap = new HashMap<>();
        for (Class clazz : allLoadedClasses) {
            if (clazz.getClassLoader() != null) {
                classLoaderMap.put(getHashCode(clazz.getClassLoader()), clazz.getClassLoader());
            }
        }
        return classLoaderMap;
    }

    public static String getHashCode(ClassLoader classLoader) {
        return Integer.toHexString(classLoader.hashCode());
    }
}
