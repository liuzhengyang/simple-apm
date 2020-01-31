package com.github.liuzhengyang.simpleapm.agent.command;

import java.util.HashMap;
import java.util.Map;

import org.mvel2.MVELInterpretedRuntime;
import org.mvel2.ParserConfiguration;
import org.mvel2.ParserContext;
import org.mvel2.integration.VariableResolverFactory;
import org.mvel2.integration.impl.MapVariableResolverFactory;

import com.github.liuzhengyang.simpleapm.agent.util.ClassLoaderUtils;
import com.github.liuzhengyang.simpleapm.agent.util.CommandProcessUtil;
import com.github.liuzhengyang.simpleapm.agent.util.JsonUtils;

import io.vertx.core.Handler;
import io.vertx.core.cli.Argument;
import io.vertx.core.cli.CLI;
import io.vertx.core.cli.Option;
import io.vertx.ext.shell.command.CommandBuilder;
import io.vertx.ext.shell.command.CommandProcess;

public class ExpressionLanguageCommand implements ApmCommand {

    private Map<String, Object> variables = new HashMap<>();
    private VariableResolverFactory variableResolverFactory;

    public ExpressionLanguageCommand() {
        variableResolverFactory = new MapVariableResolverFactory(variables);
    }

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
            MVELInterpretedRuntime runtime = new MVELInterpretedRuntime(line, null, variableResolverFactory, parserContext);
            Object parse = runtime.parse();
            CommandProcessUtil.println(process, "%s", JsonUtils.toJson(parse));
            process.end();
        };
    }
}
