package com.github.liuzhengyang.simpleapm.agent.vertx;

import io.vertx.core.Vertx;
import io.vertx.ext.shell.command.CommandBuilder;
import io.vertx.ext.shell.command.CommandRegistry;

public class ShutdownCommand {
    public static void buildShutdownCommand(Vertx vertx) {
        CommandBuilder builder = CommandBuilder.command("shutdown");
        builder.processHandler(process -> {

            // Write a message to the console
            process.write("Shutting down...");

            // End the process
            process.end();
            vertx.close();
        });

        // Register the command
        CommandRegistry registry = CommandRegistry.getShared(vertx);
        registry.registerCommand(builder.build(vertx));
    }

}
