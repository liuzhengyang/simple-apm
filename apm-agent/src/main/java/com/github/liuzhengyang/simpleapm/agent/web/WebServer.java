package com.github.liuzhengyang.simpleapm.agent.web;

import java.text.DateFormat;
import java.time.Instant;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.liuzhengyang.simpleapm.agent.command.decompiler.ProcyonCommand;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.ext.web.templ.freemarker.FreeMarkerTemplateEngine;

/**
 * @author liuzhengyang
 * Make something people want.
 * 2020/3/3
 */
public class WebServer {
    private static final Logger logger = LoggerFactory.getLogger(WebServer.class);

    private static EventBus eventBus;

    public static void startWebServer(Vertx vertx) {
        logger.info("Start web server");

        // To simplify the development of the web components we use a Router to route all HTTP requests
        // to organize our code in a reusable way.
        final Router router = Router.router(vertx);

        // In order to use a Thymeleaf template we first need to create an engine
        final FreeMarkerTemplateEngine engine = FreeMarkerTemplateEngine.create(vertx);

        // Entry point to the application, this will render a custom JADE template.
        router.route(HttpMethod.GET, "/source/:className").handler(ctx -> {
            // we define a hardcoded title for our application
            String className = ctx.request().getParam("className");
            String decompiledClassContent = StringUtils.isBlank(className) ? "" : ProcyonCommand.getDecompiledClass(className);
            JsonObject data = new JsonObject()
                    .put("content", decompiledClassContent);

            // and now delegate to the engine to render it.
            ClassLoader classLoader = WebServer.class.getClassLoader();
            System.out.println("CurrentClassLoader is " + classLoader);
            System.out.println("source.ftl in " +  classLoader.getResource("templates/source.ftl"));

            engine.render(data, "templates/source.ftl", res -> {
                if (res.succeeded()) {
                    ctx.response().end(res.result());
                } else {
                    ctx.fail(res.cause());
                }
            });
        });

        router.route("/static/*").handler(StaticHandler.create());
        // start a HTTP web server on port 8080
        vertx.createHttpServer().requestHandler(router).listen(8000);
        startSockJS(vertx);
    }

    private static void startSockJS(Vertx vertx) {
        Router router = Router.router(vertx);
        // Allow events for the designated addresses in/out of the event bus bridge
        BridgeOptions opts = new BridgeOptions()
                .addInboundPermitted(new PermittedOptions().setAddress("chat.to.server"))
                .addOutboundPermitted(new PermittedOptions().setAddress("chat.to.client"));

        // Create the event bus bridge and add it to the router.
        SockJSHandler ebHandler = SockJSHandler.create(vertx);
        ebHandler.bridge(opts);
        router.route("/eventbus/*").handler(ebHandler);

        // Create a router endpoint for the static content.
        router.route().handler(StaticHandler.create());

        // Start the web server and tell it to use the router to handle requests.
        vertx.createHttpServer().requestHandler(router).listen(8080);

        eventBus = vertx.eventBus();

        // Register to listen for messages coming IN to the server
        eventBus.consumer("chat.to.server").handler(message -> {
            // Create a timestamp string
            String timestamp = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(Date.from(Instant.now()));
            // Send the message back out to all clients with the timestamp prepended.
            eventBus.publish("chat.to.client", timestamp + ": " + message.body());
        });
    }

    public static void send(String msg) {
        eventBus.publish("chat.to.client", msg);
    }
}
