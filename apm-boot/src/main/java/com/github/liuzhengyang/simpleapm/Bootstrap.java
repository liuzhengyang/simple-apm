package com.github.liuzhengyang.simpleapm;

import java.io.IOException;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

/**
 * @author liuzhengyang
 * Created on 2019-10-28
 */
public class Bootstrap {
    public static void main(String[] args) {
        String agentPath = args[0];
        String targetPid = args[1];
        try {
            VirtualMachine attach = VirtualMachine.attach(targetPid);
            attach.loadAgent(agentPath);
            attach.detach();
        } catch (AttachNotSupportedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AgentLoadException e) {
            e.printStackTrace();
        } catch (AgentInitializationException e) {
            e.printStackTrace();
        }

    }

    private static String getToolsJarPath() {
        return null;
    }

    /**
     * 1. Try to find java home from System Property java.home
     * 2. If jdk > 8, FOUND_JAVA_HOME set to java.home
     * 3. If jdk <=8, try to find tools.jar under java.home
     *
     * @return
     */
    private static String getJavaHome() {
        return null;
    }
}
