package com.github.liuzhengyang.simpleapm.agent;

import java.io.Serializable;

import org.mvel2.MVEL;
import org.mvel2.ParserContext;


public class TestMvel {
    public static void main(String[] args) {
        ParserContext parserContext = new ParserContext();
        parserContext.addPackageImport("java.util");
        Serializable serializable = MVEL.compileExpression("new Date()", parserContext);
        Object o = MVEL.executeExpression(serializable);
        System.out.println(o);
    }
}
