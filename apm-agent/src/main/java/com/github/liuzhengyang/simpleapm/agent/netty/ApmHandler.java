package com.github.liuzhengyang.simpleapm.agent.netty;

import java.io.Serializable;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.liuzhengyang.simpleapm.agent.util.DumpUtils;
import com.github.liuzhengyang.simpleapm.agent.InstrumentationHolder;
import com.github.liuzhengyang.simpleapm.agent.util.JsonUtils;
import com.github.liuzhengyang.simpleapm.agent.asm.MonitorClassVisitor;
import com.github.liuzhengyang.simpleapm.agent.netty.ApmCommandDecoder.Command;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author liuzhengyang
 * Created on 2019-10-28
 */
public class ApmHandler extends SimpleChannelInboundHandler<ApmCommand> {
    private static final Logger logger = LoggerFactory.getLogger(ApmHandler.class);

    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ApmCommand apmCommand) throws Exception {
        logger.info("Receive command {}", apmCommand);
        Command commandType = apmCommand.getCommandType();
        ChannelContextHolder.setChannelContext(channelHandlerContext);

        switch (commandType) {
            case SEARCH_CLASS:
                String classPattern = apmCommand.getArgs().get(0);
                Pattern pattern = Pattern.compile(classPattern);
                Instrumentation instrumentation = InstrumentationHolder.getInstrumentation();
                Class[] allLoadedClasses = instrumentation.getAllLoadedClasses();
                List<Class<?>> targetClassList = new ArrayList<>();
                for (Class allLoadedClass : allLoadedClasses) {
                    if (pattern.matcher(allLoadedClass.getName()).matches()) {
                        targetClassList.add(allLoadedClass);
                    }
                }
                targetClassList.forEach(clazz -> {
                    channelHandlerContext.write(String.format("%s %s\n", clazz.getName(), clazz.getClassLoader()));
                });
                channelHandlerContext.flush();
                break;
            case WATCH:
                classPattern = apmCommand.getArgs().get(0);
                pattern = Pattern.compile(classPattern);
                instrumentation = InstrumentationHolder.getInstrumentation();
                allLoadedClasses = instrumentation.getAllLoadedClasses();
                List<String> args = apmCommand.getArgs();
                Pattern methodPattern = Pattern.compile(args.get(1));
                List<Class<?>> toInstrumentClassList = new ArrayList<>();
                for (Class loadedClass : allLoadedClasses) {
                    if (pattern.matcher(loadedClass.getName()).matches()) {
                        toInstrumentClassList.add(loadedClass);
                    }
                }
                ClassFileTransformer classFileTransformer = new ClassFileTransformer() {
                    @Override
                    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer)
                            throws IllegalClassFormatException {
                        if (pattern.matcher(className).matches()) {
                            try {
                                byte[] result = transformBytes(methodPattern, classfileBuffer);
                                DumpUtils.dump(result);
                                return result;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        return null;
                    }
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
                break;

            case MVEL:
                ParserContext parserContext = new ParserContext();
                Serializable expression = MVEL.compileExpression(apmCommand.getAllArgsString(), parserContext);
                Object result = MVEL.executeExpression(expression);
                channelHandlerContext.writeAndFlush(JsonUtils.toJson(result) + "\r\n");
                break;
            default:
                break;
        }
    }

    public static byte[] transformBytes(Pattern methodPattern, byte[] bytes) {
        ClassReader classReader = new ClassReader(bytes);
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        MonitorClassVisitor monitorClassVisitor = new MonitorClassVisitor(methodPattern, classWriter);
        classReader.accept(monitorClassVisitor, ClassReader.EXPAND_FRAMES);
        return classWriter.toByteArray();
    }

}
