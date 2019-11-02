package com.github.liuzhengyang.simpleapm.agent;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.commons.Method;

public class MonitorMethodVisitor extends AdviceAdapter {
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
