package com.github.liuzhengyang.simpleapm.agent.command;

import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.github.liuzhengyang.simpleapm.agent.InstrumentationHolder;

import io.vertx.core.Handler;
import io.vertx.ext.shell.command.CommandBuilder;
import io.vertx.ext.shell.command.CommandProcess;

public class SearchClassCommand implements ApmCommand {

    @Override
    public CommandBuilder getCommandBuilder() {
        return CommandBuilder.command("sc");
    }

    @Override
    public Handler<CommandProcess> getCommandProcessHandler() {
        return process -> {
            List<String> args = process.args();
            Pattern classPattern = Pattern.compile(args.get(0));

            // Write a message to the console
            process.write(String.format("Searching %s ...\r\n", args.get(0)));
            Instrumentation instrumentation = InstrumentationHolder.getInstrumentation();
            Class[] allLoadedClasses = instrumentation.getAllLoadedClasses();
            List<Class<?>> targetClassList = new ArrayList<>();
            for (Class allLoadedClass : allLoadedClasses) {
                if (classPattern.matcher(allLoadedClass.getName()).matches()) {
                    targetClassList.add(allLoadedClass);
                }
            }
            targetClassList.forEach(clazz -> {
                process.write(String.format("%s %s\r\n", clazz, clazz.getClassLoader()));
            });
            // End the process
            process.end();
        };
    }
}
