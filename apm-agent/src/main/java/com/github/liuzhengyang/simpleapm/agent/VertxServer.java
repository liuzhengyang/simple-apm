package com.github.liuzhengyang.simpleapm.agent;

import static com.github.liuzhengyang.simpleapm.agent.util.BannerUtil.getBanner;

import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.liuzhengyang.simpleapm.agent.command.ApmCommand;
import com.github.liuzhengyang.simpleapm.agent.command.ClassLoaderCommand;
import com.github.liuzhengyang.simpleapm.agent.command.DumpCommand;
import com.github.liuzhengyang.simpleapm.agent.command.ExpressionLanguageCommand;
import com.github.liuzhengyang.simpleapm.agent.command.SearchClassCommand;
import com.github.liuzhengyang.simpleapm.agent.command.ShutdownCommand;
import com.github.liuzhengyang.simpleapm.agent.command.WatchCommand;
import com.github.liuzhengyang.simpleapm.agent.command.decompiler.ProcyonCommand;
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
        List<Class<? extends ApmCommand>> apmCommandList = new ArrayList<>();
        apmCommandList.add(DumpCommand.class);
        apmCommandList.add(ExpressionLanguageCommand.class);
        apmCommandList.add(SearchClassCommand.class);
        apmCommandList.add(ShutdownCommand.class);
        apmCommandList.add(WatchCommand.class);
        List<Class<? extends AnnotatedCommand>> annotatedCommandList = new ArrayList<>();
        annotatedCommandList.add(ClassLoaderCommand.class);
        annotatedCommandList.add(ProcyonCommand.class);

        apmCommandList.forEach(apmCommand -> {
            try {
                ApmCommand abstractApmCommand = apmCommand.newInstance();
                abstractApmCommand.registerCommand(vertx);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        annotatedCommandList.forEach(command -> {
            CommandRegistry registry = CommandRegistry.getShared(vertx);
            logger.info("Register {}", command.getName());
            registry.registerCommand(command);
        });
    }

    public static Vertx getVertx() {
        return vertx;
    }

}
