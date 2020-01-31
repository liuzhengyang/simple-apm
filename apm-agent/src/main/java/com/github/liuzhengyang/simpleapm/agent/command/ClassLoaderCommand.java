package com.github.liuzhengyang.simpleapm.agent.command;

import java.util.Map;

import com.github.liuzhengyang.simpleapm.agent.util.ClassLoaderUtils;
import com.github.liuzhengyang.simpleapm.agent.util.CommandProcessUtil;

import io.vertx.core.cli.annotations.Name;
import io.vertx.core.cli.annotations.Summary;
import io.vertx.ext.shell.command.AnnotatedCommand;
import io.vertx.ext.shell.command.CommandProcess;

/**
 * @author liuzhengyang
 */
@Name("classloader")
@Summary("Get all class loaders")
public class ClassLoaderCommand extends AnnotatedCommand {

    @Override
    public void process(CommandProcess process) {
        Map<String, ClassLoader> allClassLoader = ClassLoaderUtils.getAllClassLoader();
        allClassLoader.forEach((hashCode, loader) -> CommandProcessUtil.println(process, "%s %s", hashCode, loader));
        process.end();
    }
}
