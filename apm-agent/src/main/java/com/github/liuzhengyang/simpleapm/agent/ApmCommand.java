package com.github.liuzhengyang.simpleapm.agent;

import java.util.List;

import com.github.liuzhengyang.simpleapm.agent.ApmCommandDecoder.Command;

/**
 * @author liuzhengyang
 * Created on 2019-10-28
 */
public class ApmCommand {
    private Command commandType;
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

    @Override
    public String toString() {
        return "ApmCommand{" +
                "commandType=" + commandType +
                ", args=" + args +
                '}';
    }
}
