package com.github.liuzhengyang.simpleapm.agent.web;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.liuzhengyang.simpleapm.agent.command.decompiler.ProcyonCommand;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.templ.freemarker.FreeMarkerTemplateEngine;

/**
 * @author liuzhengyang
 * Make something people want.
 * 2020/3/3
 */
public class WebServer {
    private static final Logger logger = LoggerFactory.getLogger(WebServer.class);

    public static void main(String[] args) {
        startWebServer(Vertx.vertx());
    }

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

        // start a HTTP web server on port 8080
        vertx.createHttpServer().requestHandler(router).listen(8000);
    }
}
