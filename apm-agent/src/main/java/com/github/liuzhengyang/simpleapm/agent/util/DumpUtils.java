package com.github.liuzhengyang.simpleapm.agent.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javassist.ClassPool;
import javassist.CtClass;

/**
 * @author liuzhengyang
 * Created on 2019-10-28
 */
public class DumpUtils {
    public static void dump(byte[] bytes) {
        ClassPool classPool = ClassPool.getDefault();
        try {
            CtClass ctClass = classPool.makeClass(new ByteArrayInputStream(bytes));
            ctClass.debugWriteFile("/tmp");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
