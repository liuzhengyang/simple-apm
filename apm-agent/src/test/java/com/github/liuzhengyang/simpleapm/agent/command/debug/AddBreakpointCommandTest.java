package com.github.liuzhengyang.simpleapm.agent.command.debug;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import com.github.liuzhengyang.simpleapm.agent.InstrumentationHolder;
import com.github.liuzhengyang.simpleapm.agent.util.DumpUtils;

import net.bytebuddy.agent.ByteBuddyAgent;

/**
 * @author liuzhengyang
 * Make something people want.
 * 2020/3/5
 */
class AddBreakpointCommandTest {
    @Test
    public void testDebug() {
        Instrumentation install = ByteBuddyAgent.install();
        install.addTransformer((loader, className, classBeingRedefined, protectionDomain, classfileBuffer) -> {
            if (StringUtils.contains(className, "DebugTest")) {
                byte[] bytes = AddBreakpointCommand.doTransform(classfileBuffer, 20);
                DumpUtils.dump(bytes);
                return bytes;
            }
            return null;
        }, true);
        DebugTest debugTest = new DebugTest();
        for (int i = 0; i < 10; i++) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            debugTest.test();
        }
    }

}
