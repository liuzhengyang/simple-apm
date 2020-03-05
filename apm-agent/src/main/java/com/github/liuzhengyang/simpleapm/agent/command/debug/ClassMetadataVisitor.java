package com.github.liuzhengyang.simpleapm.agent.command.debug;

import static org.objectweb.asm.Opcodes.ASM5;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author liuzhengyang
 * Make something people need.
 * 2020/3/4
 */
public class ClassMetadataVisitor extends ClassVisitor {
    private static final Logger logger = LoggerFactory.getLogger(ClassMetadataVisitor.class);

    private ClassMeta classMeta;

    public ClassMetadataVisitor() {
        super(ASM5);
        classMeta = new ClassMeta();
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodMeta methodMeta = new MethodMeta();
        methodMeta.setName(name);
        methodMeta.setDescriptor(descriptor);
        classMeta.addMethod(methodMeta);
        return new MethodMetaVisitor(methodMeta);
    }

    static class MethodMetaVisitor extends MethodVisitor {

        private MethodMeta methodMeta;

        private Map<String, Integer> labelToLineMap = new HashMap<>();

        public MethodMetaVisitor(MethodMeta methodMeta) {
            super(ASM5);
            this.methodMeta = methodMeta;
        }

        @Override
        public void visitLineNumber(int line, Label start) {
            labelToLineMap.put(start.toString(), line);
        }

        @Override
        public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end, int index) {
            super.visitLocalVariable(name, descriptor, signature, start, end, index);
            Integer startLine = labelToLineMap.getOrDefault(start.toString(), Integer.MAX_VALUE);
            Integer endLine = labelToLineMap.getOrDefault(end.toString(), Integer.MAX_VALUE);

            logger.info("LocalVariable {}, start {}, end {}", name, startLine, endLine);
            LocalVariable localVariable = new LocalVariable(name, descriptor, startLine, endLine, index);
            methodMeta.addLocalVariable(localVariable);
        }
    }

    public ClassMeta getClassMeta() {
        return classMeta;
    }
}
