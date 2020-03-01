package com.github.liuzhengyang.simpleapm.agent.command;

import com.github.liuzhengyang.simpleapm.agent.VertxServer;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.shell.command.CommandBuilder;
import io.vertx.ext.shell.command.CommandProcess;
import io.vertx.ext.shell.command.CommandRegistry;

public class ShutdownCommand implements ApmCommand {

    @Override
    public CommandBuilder getCommandBuilder() {
        return CommandBuilder.command("shutdown");
    }

    @Override
    public Handler<CommandProcess> getCommandProcessHandler() {
        return process -> {

            // Write a message to the console
            process.write("Shutting down...");

            // End the process
            process.end();
            VertxServer.getVertx().close();
        };
    }
}
