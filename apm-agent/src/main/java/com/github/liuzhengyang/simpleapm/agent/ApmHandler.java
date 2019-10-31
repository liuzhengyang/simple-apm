package com.github.liuzhengyang.simpleapm.agent;

import static org.objectweb.asm.Opcodes.ASM5;

import java.io.Serializable;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.commons.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.liuzhengyang.simpleapm.agent.ApmCommandDecoder.Command;

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

    public static class MonitorClassVisitor extends ClassVisitor {

        private String className;
        private Pattern methodPattern;

        public MonitorClassVisitor(Pattern methodPattern, ClassWriter cw) {
            super(ASM5, cw);
            this.methodPattern = methodPattern;
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName,
                String[] interfaces) {
            super.visit(version, access, name, signature, superName, interfaces);
            this.className = name;
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature,
                String[] exceptions) {
            MethodVisitor mv = cv.visitMethod(access, name, descriptor, signature, exceptions);
            if (methodPattern.matcher(name).matches()) {
                return new MonitorMethodVisitor(className, mv, access, name, descriptor);
            } else {
                return mv;
            }
        }
    }

    public static class MonitorMethodVisitor extends AdviceAdapter {
        private final String className;
        private final String methodName;
        private final String methodDescriptor;

        protected MonitorMethodVisitor(String className, MethodVisitor methodVisitor, int access, String name,
                String descriptor) {
            super(ASM5, methodVisitor, access, name, descriptor);
            this.className = className;
            this.methodName = name;
            this.methodDescriptor = descriptor;
        }

        @Override
        protected void onMethodEnter() {
            push(className);
            push(methodName);
            loadArgArray();
            Method enterMethod = Method.getMethod("void enter (String,String,Object[])");
            invokeStatic(Type.getType(ThreadLocalMonitorTracer.class), enterMethod);
        }

        @Override
        protected void onMethodExit(int opcode) {
            push(className);
            push(methodName);
            loadReturnValue(opcode);
            Method exitMethod = Method.getMethod("void exit (String,String,Object)");
            invokeStatic(Type.getType(ThreadLocalMonitorTracer.class), exitMethod);
        }

        private void loadReturnValue(int opcode) {
            if (opcode == RETURN) {
                visitInsn(ACONST_NULL);
            } else if (opcode == ARETURN || opcode == ATHROW) {
                dup();
            } else {
                if (opcode == LRETURN || opcode == DRETURN) {
                    dup2();
                } else {
                    dup();
                }
                box(Type.getReturnType(methodDescriptor));
            }
        }
    }

    public static class ThreadLocalMonitorTracer {
        private static final ThreadLocal<Stack<Long>> stack = ThreadLocal.withInitial(Stack::new);

        public static void enter(String className, String methodName, Object[] params) {
            System.out.println("Enter");
            stack.get().push(System.nanoTime());
            logger.info("{}.{} enter params {} at {}", className, methodName, Arrays.toString(params), new Date());
            ChannelContextHolder.getCtx().writeAndFlush(String.format("%s.%s enter params: %s\n", className, methodName, Arrays.toString(params)));
        }

        public static void exit(String className, String methodName, Object result) {
            Long enterTime = stack.get().pop();
            if (enterTime == null) {
                enterTime = 0L;
            }
            long currentTime = System.nanoTime();
            long cost = currentTime - enterTime;
            logger.info("{}.{}, result {} cost {}", className, methodName, result, cost);
            ChannelContextHolder.getCtx().writeAndFlush(String.format("%s.%s return %s cost %d nano\n", className, methodName, result, cost));
        }
    }
}
