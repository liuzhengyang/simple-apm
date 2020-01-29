package com.github.liuzhengyang.simpleapm.agent.command;

import com.github.liuzhengyang.simpleapm.agent.Constants;
import com.github.liuzhengyang.simpleapm.agent.util.ClassLoaderUtils;

import io.vertx.core.Handler;
import io.vertx.core.cli.Argument;
import io.vertx.core.cli.CLI;
import io.vertx.core.cli.CommandLine;
import io.vertx.ext.shell.command.CommandBuilder;
import io.vertx.ext.shell.command.CommandProcess;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.LoaderClassPath;
import javassist.NotFoundException;

/**
 * @author liuzhengyang
 */
public class DumpCommand implements ApmCommand {

    @Override
    public CommandBuilder getCommandBuilder() {
        CLI cli = CLI.create("dump").
                addArgument(new Argument().setRequired(true).setArgName("class-name")).
                addArgument(new Argument().setRequired(false).setArgName("classloader"));

        return CommandBuilder.command(cli);
    }

    @Override
    public Handler<CommandProcess> getCommandProcessHandler() {
        return process -> {
            CommandLine commandLine = process.commandLine();
            String className = commandLine.getArgumentValue("class-name");
            String classLoaderHashCode = commandLine.getArgumentValue("classloader");
            ClassLoader classLoader = ClassLoaderUtils.getLoader(classLoaderHashCode);
            ClassPool classPool = new ClassPool();
            classPool.appendClassPath(new LoaderClassPath(classLoader));
            try {
                CtClass ctClass = classPool.get(className);
                String dir = "/tmp/";
                ctClass.debugWriteFile(dir);
                String classLocation = dir + className.replace('.', '/') + ".class";
                process.write(String.format("Write to %s" + Constants.CRLF, classLocation));
            } catch (NotFoundException e) {
                e.printStackTrace();
            }
            process.end();
        };
    }
}
