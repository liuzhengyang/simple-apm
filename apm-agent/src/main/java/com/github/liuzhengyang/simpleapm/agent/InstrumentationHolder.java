package com.github.liuzhengyang.simpleapm.agent;

import java.lang.instrument.Instrumentation;

/**
 * @author liuzhengyang
 * Created on 2019-10-28
 */
public class InstrumentationHolder {


    private static Instrumentation instrumentation;

    public static void setInstrumentation(Instrumentation instrumentation) {
        InstrumentationHolder.instrumentation = instrumentation;
    }

    public static Instrumentation getInstrumentation() {
        return instrumentation;
    }

}
