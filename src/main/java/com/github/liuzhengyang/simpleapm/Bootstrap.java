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
}
