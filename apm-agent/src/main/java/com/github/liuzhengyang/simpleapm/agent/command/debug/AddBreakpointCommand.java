package com.github.liuzhengyang.simpleapm.agent.command.debug;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ASM5;
import static org.objectweb.asm.Opcodes.ASM7;

import java.io.ByteArrayInputStream;
import java.lang.instrument.UnmodifiableClassException;

import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.liuzhengyang.simpleapm.agent.InstrumentationHolder;
import com.github.liuzhengyang.simpleapm.agent.util.DumpUtils;

import io.vertx.core.cli.annotations.Argument;
import io.vertx.core.cli.annotations.Name;
import io.vertx.ext.shell.command.AnnotatedCommand;
import io.vertx.ext.shell.command.CommandProcess;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.Modifier;

/**
 * @author liuzhengyang
 * Make something people want.
 * 2020/3/3
 */
@Name("addBreakpoint")
public class AddBreakpointCommand extends AnnotatedCommand {
    private static final Logger logger = LoggerFactory.getLogger(AddBreakpointCommand.class);

    private String className;
    private int lineNumber;

    @Argument(argName = "className", index = 0)
    public void setClassName(String className) {
        this.className = className;
    }

    @Argument(argName = "lineNumber", index = 1)
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    @Override
    public void process(CommandProcess commandProcess) {
        InstrumentationHolder.getInstrumentation().addTransformer((loader, className, classBeingRedefined, protectionDomain, classfileBuffer) -> {
            if (StringUtils.equals(AddBreakpointCommand.this.className.replace(".", "/"), className)) {
                byte[] result = doTransform(classfileBuffer, lineNumber);
                DumpUtils.dump(result);
                return result;
            }
            return null;
        }, true);
        Class[] allLoadedClasses = InstrumentationHolder.getInstrumentation().getAllLoadedClasses();
        for (Class clazz : allLoadedClasses) {
            if (StringUtils.equals(className, clazz.getName())) {
                try {
                    logger.info("retransform {}", className);
                    InstrumentationHolder.getInstrumentation().retransformClasses(clazz);
                } catch (UnmodifiableClassException e) {
                    e.printStackTrace();
                }
            }
        }
        commandProcess.end();
    }

    private static byte[] doTransform(byte[] origin, int lineNumber) {
        try {
            CtClass ctClass = ClassPool.getDefault().makeClass(new ByteArrayInputStream(origin));
            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
            ClassReader classReader = new ClassReader(origin);
            DebugClassVisitor debugClassVisitor = new DebugClassVisitor(ctClass.getName(), lineNumber, ctClass, classWriter);
            classReader.accept(debugClassVisitor, ClassReader.EXPAND_FRAMES);
            return classWriter.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    static class DebugClassVisitor extends ClassVisitor {

        private String className;
        private int lineNumber;
        private CtClass ctClass;

        public DebugClassVisitor(String className, int lineNumber, CtClass ctClass, ClassWriter cw) {
            super(ASM7, cw);
            this.lineNumber = lineNumber;
            this.className = className;
            this.ctClass = ctClass;
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);
            return new DebugMethodVisitor(className, methodVisitor, access, name, descriptor, ctClass, lineNumber);
        }
    }

    static class DebugMethodVisitor extends GeneratorAdapter {

        private String className;
        private int lineNumber;
        private String methodName;
        private CtClass ctClass;

        public DebugMethodVisitor(String className, MethodVisitor mv, int access, String methodName, String methodDescriptor, CtClass ctClass, int lineNumber) {
            super(ASM5, mv, access, methodName, methodDescriptor);
            this.className = className;
            this.methodName = methodName;
            this.ctClass = ctClass;
            this.lineNumber = lineNumber;
        }

        @Override
        public void visitLineNumber(int line, Label start) {
            super.visitLineNumber(line, start);
            logger.info("Visit line number {}, {}, {}", line, start, methodName);
            if (lineNumber == line) {
                logger.info("add debug line number {}, {}, {}", line, start, methodName);
                // 如果这里有断点
                // 获取字段、局部变量数据、
                // 打印线程栈
                for (CtField field : ctClass.getDeclaredFields()) {
                    boolean isStatic = Modifier.isStatic(field.getModifiers());
                    String name = field.getName();
                    String signature = field.getSignature();
                    String ownerClass = field.getDeclaringClass().getName().replace(".", "/");
                    mv.visitLdcInsn(name);

                    if (isStatic) {
                        visitFieldInsn(Opcodes.GETSTATIC, field.getDeclaringClass().getName().replace(".", "/"), field.getName(), field.getSignature());
                    } else {
                        mv.visitVarInsn(ALOAD, 0);
                        mv.visitFieldInsn(Opcodes.GETFIELD, ownerClass, name, signature);
                    }
                    logger.info("push instance field {}, {}", ownerClass, field.getName());
                    box(Type.getType(signature));
                    invokeStatic(Type.getType(DebugUtils.class), Method.getMethod("void printField(java.lang.String, java.lang.Object)"));
                }
            }
        }

    }
}
