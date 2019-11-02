package com.github.liuzhengyang.simpleapm.agent.vertx;

import java.util.List;
import java.util.stream.Collectors;

import org.mvel2.MVEL;

import com.github.liuzhengyang.simpleapm.agent.Constants;
import com.github.liuzhengyang.simpleapm.agent.util.JsonUtils;

import io.vertx.core.Vertx;
import io.vertx.ext.shell.command.CommandBuilder;
import io.vertx.ext.shell.command.CommandRegistry;

public class ExpressionLanguageCommand {
    public static void buildExpressionCommand(Vertx vertx) {
        CommandBuilder builder = CommandBuilder.command("el");
        builder.processHandler(process -> {

            // Write a message to the console
            List<String> args = process.args();
            String line = args.stream()
                    .collect(Collectors.joining(" "));
            Object eval = MVEL.eval(line);

            process.write(JsonUtils.toJson(eval) + Constants.CRLF);
            // End the process
            process.end();
        });

        // Register the command
        CommandRegistry registry = CommandRegistry.getShared(vertx);
        registry.registerCommand(builder.build(vertx));
    }
}
