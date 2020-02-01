package com.github.liuzhengyang.simpleapm.agent.command;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import com.github.liuzhengyang.simpleapm.agent.InstrumentationHolder;
import com.github.liuzhengyang.simpleapm.agent.Terminal;
import com.github.liuzhengyang.simpleapm.agent.asm.MonitorClassVisitor;
import com.github.liuzhengyang.simpleapm.agent.util.DumpUtils;
import com.github.liuzhengyang.simpleapm.agent.util.ObjectFormatter;
import com.github.liuzhengyang.simpleapm.agent.VertxServer;

import io.vertx.core.Handler;
import io.vertx.core.cli.Argument;
import io.vertx.core.cli.CLI;
import io.vertx.core.cli.Option;
import io.vertx.ext.shell.cli.CliToken;
import io.vertx.ext.shell.cli.Completion;
import io.vertx.ext.shell.command.CommandBuilder;
import io.vertx.ext.shell.command.CommandProcess;

public class WatchCommand implements ApmCommand {

    // refactoring: magic number, comment
    @Override
    public Handler<Completion> getCompletionHandler() {
        return event -> {
            List<CliToken> cliTokens = event.lineTokens();
            Instrumentation instrumentation = InstrumentationHolder.getInstrumentation();
            Class[] allLoadedClasses = instrumentation.getAllLoadedClasses();
            if (cliTokens.size() == 1) {
                List<String> allClasses = new ArrayList<>();
                for (Class allLoadedClass : allLoadedClasses) {
                    allClasses.add(allLoadedClass.getName());
                }
                event.complete(allClasses);
            } else if (cliTokens.size() == 2) {
                CliToken firstToken = cliTokens.get(1);
                Pattern classPattern = Pattern.compile(firstToken.value());
                List<String> allClasses = new ArrayList<>();
                for (Class allLoadedClass : allLoadedClasses) {
                    if (firstToken.isBlank()) {
                        allClasses.add(allLoadedClass.getName());
                    } else if (classPattern.matcher(allLoadedClass.getName()).matches() || allLoadedClass.getName().startsWith(firstToken.value())) {
                        allClasses.add(allLoadedClass.getName());
                    }
                }
                event.complete(allClasses);
            } else if (cliTokens.size() == 3 | cliTokens.size() == 4) {
                CliToken firstToken = cliTokens.get(1);
                Pattern classPattern = Pattern.compile(firstToken.value());

                List<Class> allClasses = new ArrayList<>();
                for (Class allLoadedClass : allLoadedClasses) {
                    if (classPattern.matcher(allLoadedClass.getName()).matches()) {
                        allClasses.add(allLoadedClass);
                    }
                }

                boolean hasMethodPattern = cliTokens.size() == 4;
                Pattern methodPattern = null;

                String methodPatternName = null;
                if (hasMethodPattern) {
                    methodPatternName = cliTokens.get(3).value();
                    methodPattern = Pattern.compile(methodPatternName);
                }

                List<String> methodList = new ArrayList<>();
                for (Class clazz : allClasses) {
                    Method[] declaredMethods = clazz.getDeclaredMethods();
                    for (Method method : declaredMethods) {
                        if (hasMethodPattern) {
                            if (methodPattern.matcher(method.getName()).matches()
                                    || method.getName().startsWith(methodPatternName)) {
                                methodList.add(method.getName());
                            }
                        } else {
                            methodList.add(method.getName());
                        }

                    }
                }
                event.complete(methodList);
            } else {
                event.complete(Collections.emptyList());
            }
        };
    }

    @Override
    public CommandBuilder getCommandBuilder() {
        CLI cli = CLI.create("watch").
                addArgument(new Argument().setArgName("class-pattern")).
                addArgument(new Argument().setArgName("method-pattern")).
                addArgument(new Argument().setRequired(false).setArgName("method-pattern")).
                addOption(new Option().setArgName("help").setShortName("h").setLongName("help"));
        return CommandBuilder.command(cli);
    }

    @Override
    public Handler<CommandProcess> getCommandProcessHandler() {
        return this::watchProcessHelper;
    }

    private void watchProcessHelper(CommandProcess process) {
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
            Terminal.write("Reset \r\n");
            toInstrumentClassList.forEach(clazz -> {
                try {
                    InstrumentationHolder.getInstrumentation().retransformClasses(clazz);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            });
            Terminal.getCurrentProcess().end();
        });
    }

    public static byte[] transformBytes(Pattern methodPattern, byte[] bytes) {
        ClassReader classReader = new ClassReader(bytes);
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        MonitorClassVisitor monitorClassVisitor = new MonitorClassVisitor(methodPattern, classWriter);
        classReader.accept(monitorClassVisitor, ClassReader.EXPAND_FRAMES);
        return classWriter.toByteArray();
    }
}
