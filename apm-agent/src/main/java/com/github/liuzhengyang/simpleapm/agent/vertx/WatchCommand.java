package com.github.liuzhengyang.simpleapm.agent.vertx;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import com.github.liuzhengyang.simpleapm.agent.InstrumentationHolder;
import com.github.liuzhengyang.simpleapm.agent.asm.MonitorClassVisitor;
import com.github.liuzhengyang.simpleapm.agent.util.DumpUtils;
import com.github.liuzhengyang.simpleapm.agent.util.ObjectFormatter;

import io.vertx.core.Vertx;
import io.vertx.core.cli.Argument;
import io.vertx.core.cli.CLI;
import io.vertx.core.cli.Option;
import io.vertx.ext.shell.command.CommandBuilder;
import io.vertx.ext.shell.command.CommandRegistry;

public class WatchCommand {

    public static void buildWatchCommand(Vertx vertx) {
        CLI cli = CLI.create("watch").
                addArgument(new Argument().setArgName("my-arg")).
                addOption(new Option().setArgName("help").setShortName("h").setLongName("help"));
        CommandBuilder builder = CommandBuilder.command(cli);
        builder.processHandler(process -> {
            VertxServer.currentProcess = process;
            List<String> args = process.args();
            Instrumentation instrumentation = InstrumentationHolder.getInstrumentation();
            Class[] allLoadedClasses;
            Pattern classPattern = Pattern.compile(args.get(0));
            allLoadedClasses = instrumentation.getAllLoadedClasses();
            Pattern methodPattern = Pattern.compile(args.get(1));
            if (args.size() > 2) {
                String resultPattern = args.get(2);
                ObjectFormatter.setPattern(resultPattern);
            }
            List<Class<?>> toInstrumentClassList = new ArrayList<>();
            for (Class loadedClass : allLoadedClasses) {
                if (classPattern.matcher(loadedClass.getName()).matches()) {
                    toInstrumentClassList.add(loadedClass);
                }
            }
            ClassFileTransformer classFileTransformer = (loader, className, classBeingRedefined, protectionDomain, classfileBuffer) -> {
                if (classPattern.matcher(className).matches()) {
                    try {
                        byte[] result = transformBytes(methodPattern, classfileBuffer);
                        DumpUtils.dump(result);
                        return result;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return null;
            };
            try {
                InstrumentationHolder.getInstrumentation()
                        .addTransformer(classFileTransformer, true);
                toInstrumentClassList.forEach(clazz -> {
                    try {
                        InstrumentationHolder.getInstrumentation().retransformClasses(clazz);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                });
            } finally {
                InstrumentationHolder.getInstrumentation().removeTransformer(classFileTransformer);
            }
            process.interruptHandler(v -> {
                VertxServer.currentProcess.write("Reset \r\n");
                toInstrumentClassList.forEach(clazz -> {
                    try {
                        InstrumentationHolder.getInstrumentation().retransformClasses(clazz);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                });
                VertxServer.currentProcess.end();
            });
        });

        // Register the command
        CommandRegistry registry = CommandRegistry.getShared(vertx);
        registry.registerCommand(builder.build(vertx));
    }

    public static byte[] transformBytes(Pattern methodPattern, byte[] bytes) {
        ClassReader classReader = new ClassReader(bytes);
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        MonitorClassVisitor monitorClassVisitor = new MonitorClassVisitor(methodPattern, classWriter);
        classReader.accept(monitorClassVisitor, ClassReader.EXPAND_FRAMES);
        return classWriter.toByteArray();
    }
}
