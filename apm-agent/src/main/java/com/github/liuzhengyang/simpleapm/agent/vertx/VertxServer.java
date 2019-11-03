package com.github.liuzhengyang.simpleapm.agent.vertx;

import static com.github.liuzhengyang.simpleapm.agent.util.BannerUtil.getBanner;

import java.lang.instrument.Instrumentation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.liuzhengyang.simpleapm.agent.InstrumentationHolder;
import com.github.liuzhengyang.simpleapm.agent.Looper;

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
        Looper.asyncLoop();
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
        );
        WatchCommand.buildWatchCommand(vertx);
        ShutdownCommand.buildShutdownCommand(vertx);
        SearchClassCommand.buildSearchClassCommand(vertx);
        ExpressionLanguageCommand.buildExpressionCommand(vertx);
        ClassLoaderCommand.buildClassLoaderCommand(vertx);

        service.start();
        logger.info("Server started at {}", TCP_PORT);
    }

}
