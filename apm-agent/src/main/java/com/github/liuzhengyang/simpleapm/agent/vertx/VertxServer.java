package com.github.liuzhengyang.simpleapm.agent.vertx;

import static com.github.liuzhengyang.simpleapm.agent.util.BannerUtil.getBanner;

import java.lang.instrument.Instrumentation;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.liuzhengyang.simpleapm.agent.InstrumentationHolder;
import com.github.liuzhengyang.simpleapm.agent.command.ApmCommand;
import com.github.liuzhengyang.simpleapm.example.Looper;

import io.vertx.core.Vertx;
import io.vertx.ext.shell.ShellService;
import io.vertx.ext.shell.ShellServiceOptions;
import io.vertx.ext.shell.command.CommandProcess;
import io.vertx.ext.shell.term.HttpTermOptions;
import io.vertx.ext.shell.term.TelnetTermOptions;
import net.bytebuddy.agent.ByteBuddyAgent;

public class VertxServer {
    private static final Logger logger = LoggerFactory.getLogger(VertxServer.class);

    public static Vertx vertx = Vertx.vertx();

    public static CommandProcess currentProcess = null;

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
        logger.info("Server started at {}", TCP_PORT);
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
    }

    public static Vertx getVertx() {
        return vertx;
    }

}
