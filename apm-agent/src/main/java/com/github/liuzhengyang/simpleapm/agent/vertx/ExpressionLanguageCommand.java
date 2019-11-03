package com.github.liuzhengyang.simpleapm.agent.vertx;

import java.util.List;
import java.util.stream.Collectors;

import org.mvel2.MVEL;
import org.mvel2.ParserConfiguration;
import org.mvel2.ParserContext;

import com.github.liuzhengyang.simpleapm.agent.Constants;
import com.github.liuzhengyang.simpleapm.agent.util.ClassLoaderUtils;
import com.github.liuzhengyang.simpleapm.agent.util.JsonUtils;

import io.vertx.core.Vertx;
import io.vertx.core.cli.CommandLine;
import io.vertx.ext.shell.command.CommandBuilder;
import io.vertx.ext.shell.command.CommandRegistry;

public class ExpressionLanguageCommand {
    public static void buildExpressionCommand(Vertx vertx) {
        CommandBuilder builder = CommandBuilder.command("el");
        builder.processHandler(process -> {

            String classLoaderHashCode = process.args().size() > 1 ? process.args().get(1) : null;
            // Write a message to the console
            List<String> args = process.args();
            String line = args.stream()
                    .collect(Collectors.joining(" "));

            ParserConfiguration parserConfiguration = new ParserConfiguration();
            if (classLoaderHashCode != null) {
                parserConfiguration.setClassLoader(ClassLoaderUtils.getLoader(classLoaderHashCode));
            }
            ParserContext parserContext = new ParserContext(parserConfiguration);
            Object eval = MVEL.eval(line, parserContext);
            process.write(JsonUtils.toJson(eval) + Constants.CRLF);
            // End the process
            process.end();
        });

        // Register the command
        CommandRegistry registry = CommandRegistry.getShared(vertx);
        registry.registerCommand(builder.build(vertx));
    }
}
