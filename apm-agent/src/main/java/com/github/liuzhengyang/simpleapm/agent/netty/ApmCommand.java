package com.github.liuzhengyang.simpleapm.agent.netty;

import java.util.List;

import com.github.liuzhengyang.simpleapm.agent.netty.ApmCommandDecoder.Command;

/**
 * @author liuzhengyang
 * Created on 2019-10-28
 */
public class ApmCommand {
    private Command commandType;
    private String allArgsString;
    private List<String> args;

    public Command getCommandType() {
        return commandType;
    }

    public void setCommandType(Command commandType) {
        this.commandType = commandType;
    }

    public List<String> getArgs() {
        return args;
    }

    public void setArgs(List<String> args) {
        this.args = args;
    }

    public String getAllArgsString() {
        return allArgsString;
    }

    public ApmCommand setAllArgsString(String allArgsString) {
        this.allArgsString = allArgsString;
        return this;
    }

    @Override
    public String toString() {
        return "ApmCommand{" +
                "commandType=" + commandType +
                ", args=" + args +
                '}';
    }
}
