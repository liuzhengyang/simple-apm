package com.github.liuzhengyang.simpleapm.agent;

import static org.objectweb.asm.Opcodes.ASM5;

import java.util.regex.Pattern;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

public class MonitorClassVisitor extends ClassVisitor {

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
