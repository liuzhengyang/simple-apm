package com.github.liuzhengyang.simpleapm.agent;

import java.lang.instrument.Instrumentation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author liuzhengyang
 * Created on 2019-10-28
 */
public class ApmAgent {
    private static final Logger logger = LoggerFactory.getLogger(ApmAgent.class);

    public static void premain(String args, Instrumentation instrumentation) {
        logger.info("Premain {} ", args);
        doInstrument(args, instrumentation);
    }

    public static void agentmain(String args, Instrumentation instrumentation) {
        logger.info("Agent main {} ", args);
        doInstrument(args, instrumentation);
    }

    private static void doInstrument(String args, Instrumentation instrumentation) {
        InstrumentationHolder.setInstrumentation(instrumentation);
        BootstrapServer.start();
    }
}
