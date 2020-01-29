package com.github.liuzhengyang.simpleapm.agent.command;

import java.io.Serializable;

import org.mvel2.MVEL;
import org.mvel2.ParserConfiguration;
import org.mvel2.ParserContext;

import com.github.liuzhengyang.simpleapm.agent.Constants;
import com.github.liuzhengyang.simpleapm.agent.util.ClassLoaderUtils;
import com.github.liuzhengyang.simpleapm.agent.util.JsonUtils;

import io.vertx.core.Handler;
import io.vertx.core.cli.Argument;
import io.vertx.core.cli.CLI;
import io.vertx.core.cli.Option;
import io.vertx.ext.shell.command.CommandBuilder;
import io.vertx.ext.shell.command.CommandProcess;

public class ExpressionLanguageCommand implements ApmCommand {

    @Override
    public CommandBuilder getCommandBuilder() {
        CLI cli = CLI.create("el").
                addArgument(new Argument().setRequired(true).setArgName("expression")).
                addArgument(new Argument().setRequired(false).setArgName("classloader")).
                addArgument(new Argument().setRequired(false).setArgName("imports")).
                addOption(new Option().setArgName("help").setShortName("h").setLongName("help"));

        return CommandBuilder.command(cli);
    }

    @Override
    public Handler<CommandProcess> getCommandProcessHandler() {
        return process -> {
            String classLoaderHashCode = process.commandLine().getArgumentValue("classloader");
            String line = process.commandLine().getArgumentValue("expression");
            ParserConfiguration parserConfiguration = new ParserConfiguration();
            ClassLoader classLoader = ExpressionLanguageCommand.class.getClassLoader();
            if (classLoaderHashCode != null) {
                classLoader = ClassLoaderUtils.getLoader(classLoaderHashCode);
                parserConfiguration.setClassLoader(classLoader);
                System.out.println(String.format("ClassLoader is %s %s", classLoaderHashCode, classLoader));
            }
            ParserContext parserContext = new ParserContext(parserConfiguration);
            String imports = process.commandLine().getArgumentValue("imports");
            if (imports != null) {
                String[] importSplits = imports.split(",");
                for (String importClass : importSplits) {
                    try {
                        Class<?> importClazz = classLoader.loadClass(importClass);
                        parserContext.addImport(importClass.substring(importClass.lastIndexOf(".") + 1), importClazz);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
            Serializable serializable = MVEL.compileExpression(line, parserContext);
            Object eval = MVEL.executeExpression(serializable);
            process.write(JsonUtils.toJson(eval) + Constants.CRLF);
            process.end();
        };
    }
}
