package com.github.liuzhengyang.simpleapm.agent;

import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Vertx;
import io.vertx.ext.shell.ShellService;
import io.vertx.ext.shell.ShellServiceOptions;
import io.vertx.ext.shell.command.CommandBuilder;
import io.vertx.ext.shell.command.CommandProcess;
import io.vertx.ext.shell.command.CommandRegistry;
import io.vertx.ext.shell.term.TelnetTermOptions;
import net.bytebuddy.agent.ByteBuddyAgent;

public class VertxServer {
    private static final Logger logger = LoggerFactory.getLogger(VertxServer.class);

    public static Vertx vertx = Vertx.vertx();

    public static CommandProcess currentProcess = null;

    public static void main(String[] args) {
        Instrumentation install = ByteBuddyAgent.install();
        InstrumentationHolder.setInstrumentation(install);
        Looper.asyncLoop();
        startShellServer();
    }

    public static void startShellServer() {
        ShellService service = ShellService.create(vertx,
                new ShellServiceOptions().setTelnetOptions(
                        new TelnetTermOptions().
                                setHost("localhost").
                                setPort(4000)
                )
        );
        WatchCommand.buildWatchCommand(vertx);
        buildShutdownCommand(vertx);
        buildSearchClassCommand(vertx);
        service.start();
    }

    private static void buildSearchClassCommand(Vertx vertx) {
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

    private static void buildShutdownCommand(Vertx vertx) {
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
