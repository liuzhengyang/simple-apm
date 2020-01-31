package com.github.liuzhengyang.simpleapm.agent;

import static com.github.liuzhengyang.simpleapm.agent.util.BannerUtil.getBanner;

import java.lang.instrument.Instrumentation;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.liuzhengyang.simpleapm.agent.command.ApmCommand;
import com.github.liuzhengyang.simpleapm.example.Looper;

import io.vertx.core.Vertx;
import io.vertx.ext.shell.ShellService;
import io.vertx.ext.shell.ShellServiceOptions;
import io.vertx.ext.shell.command.AnnotatedCommand;
import io.vertx.ext.shell.command.CommandRegistry;
import io.vertx.ext.shell.term.HttpTermOptions;
import io.vertx.ext.shell.term.TelnetTermOptions;
import net.bytebuddy.agent.ByteBuddyAgent;

public class VertxServer {
    private static final Logger logger = LoggerFactory.getLogger(VertxServer.class);

    public static Vertx vertx = Vertx.vertx();

    private static final int TCP_PORT = 6000;
    private static final int HTTP_PORT = 5000;

    public static void main(String[] args) {
        Instrumentation install = ByteBuddyAgent.install();
        InstrumentationHolder.setInstrumentation(install);
        Looper.startAsync();
        startShellServer();
    }

    public static void startShellServer() {
        ShellService service = ShellService.create(vertx,
                new ShellServiceOptions()
                        .setWelcomeMessage(getBanner())
                        .setTelnetOptions(new TelnetTermOptions()
                                .setPort(TCP_PORT))
                        .setHttpOptions(new HttpTermOptions()
                                .setPort(HTTP_PORT))
                        .setSessionTimeout(TimeUnit.DAYS.toMillis(1))
        );
        registerCommands();
        service.start();
        logger.info("Tcp Server started at {}", TCP_PORT);
        logger.info("Http Server started at {}, visit http://localhost:{}/shell.html", HTTP_PORT, HTTP_PORT);
    }

    private static void registerCommands() {
        Reflections reflections = new Reflections("com.github.liuzhengyang.simpleapm");
        Set<Class<? extends ApmCommand>> allApmCommands = reflections.getSubTypesOf(ApmCommand.class);
        allApmCommands.forEach(apmCommand -> {
            try {
                ApmCommand abstractApmCommand = apmCommand.newInstance();
                abstractApmCommand.registerCommand(vertx);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        reflections.getSubTypesOf(AnnotatedCommand.class).forEach(command -> {
            CommandRegistry registry = CommandRegistry.getShared(vertx);
            registry.registerCommand(command);
        });
    }

    public static Vertx getVertx() {
        return vertx;
    }

}
