package com.github.liuzhengyang.simpleapm.agent.vertx;

import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.github.liuzhengyang.simpleapm.agent.InstrumentationHolder;

import io.vertx.core.Vertx;
import io.vertx.ext.shell.command.CommandBuilder;
import io.vertx.ext.shell.command.CommandRegistry;

public class SearchClassCommand {

    public static void buildSearchClassCommand(Vertx vertx) {
        CommandBuilder builder = CommandBuilder.command("sc");
        builder.processHandler(process -> {
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
        });

        // Register the command
        CommandRegistry registry = CommandRegistry.getShared(vertx);
        registry.registerCommand(builder.build(vertx));
    }

}
