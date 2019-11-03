package com.github.liuzhengyang.simpleapm.agent.vertx;

import java.util.Map;

import com.github.liuzhengyang.simpleapm.agent.Constants;
import com.github.liuzhengyang.simpleapm.agent.util.ClassLoaderUtils;

import io.vertx.core.Vertx;
import io.vertx.ext.shell.command.CommandBuilder;
import io.vertx.ext.shell.command.CommandRegistry;

public class ClassLoaderCommand {
    public static void buildClassLoaderCommand(Vertx vertx) {
        CommandBuilder builder = CommandBuilder.command("classloader");
        builder.processHandler(process -> {

            Map<String, ClassLoader> allClassLoader = ClassLoaderUtils.getAllClassLoader();
            allClassLoader.forEach((hashCode, loader) -> {
                process.write(String.format("%s %s%s", hashCode, loader, Constants.CRLF));
            });

            process.end();
        });

        // Register the command
        CommandRegistry registry = CommandRegistry.getShared(vertx);
        registry.registerCommand(builder.build(vertx));
    }
}
