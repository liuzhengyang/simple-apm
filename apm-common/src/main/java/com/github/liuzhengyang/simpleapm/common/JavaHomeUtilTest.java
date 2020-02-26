package com.github.liuzhengyang.simpleapm.common;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import org.junit.jupiter.api.Test;

/**
 * @author liuzhengyang
 * Make something people want.
 * 2020/2/26
 */
class JavaHomeUtilTest {

    @Test
    void getJavaHomeDir() {
        System.out.println(JavaHomeUtil.getJavaHomeDir());
    }

    @Test
    void getToolsJar() {
        System.out.println(JavaHomeUtil.getToolsJar());
    }
}
