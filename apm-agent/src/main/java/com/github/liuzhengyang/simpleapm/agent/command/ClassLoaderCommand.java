package com.github.liuzhengyang.simpleapm.agent.command;

import java.util.Map;

import com.github.liuzhengyang.simpleapm.agent.util.ClassLoaderUtils;
import com.github.liuzhengyang.simpleapm.agent.util.CommandProcessUtil;

import io.vertx.core.Handler;
import io.vertx.ext.shell.command.CommandBuilder;
import io.vertx.ext.shell.command.CommandProcess;

/**
 * @author liuzhengyang
 */
public class ClassLoaderCommand implements ApmCommand {

    @Override
    public CommandBuilder getCommandBuilder() {
        return CommandBuilder.command("classloader");
    }

    @Override
    public Handler<CommandProcess> getCommandProcessHandler() {
        return process -> {
            Map<String, ClassLoader> allClassLoader = ClassLoaderUtils.getAllClassLoader();
            allClassLoader.forEach((hashCode, loader) -> CommandProcessUtil.println(process, "%s %s", hashCode, loader));
            process.end();
        };
    }
}
