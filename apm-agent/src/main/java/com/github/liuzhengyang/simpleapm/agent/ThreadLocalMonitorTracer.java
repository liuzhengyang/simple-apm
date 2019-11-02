package com.github.liuzhengyang.simpleapm.agent;

import java.util.Arrays;
import java.util.Date;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadLocalMonitorTracer {
    private static final Logger logger = LoggerFactory.getLogger(ThreadLocalMonitorTracer.class);

    private static final ThreadLocal<Stack<Long>> stack = ThreadLocal.withInitial(Stack::new);

    public static void enter(String className, String methodName, Object[] params) {
        System.out.println("Enter");
        stack.get().push(System.nanoTime());
        logger.info("{}.{} enter params {} at {}", className, methodName, Arrays.toString(params), new Date());
        Terminal.write(String.format("%s.%s enter params: %s\n", className, methodName, Arrays.toString(params)));
    }

    public static void exit(String className, String methodName, Object result) {
        Long enterTime = stack.get().pop();
        if (enterTime == null) {
            enterTime = 0L;
        }
        long currentTime = System.nanoTime();
        long cost = currentTime - enterTime;
        logger.info("{}.{}, result {} cost {}", className, methodName, result, cost);
        Terminal.write(String.format("%s.%s return %s cost %d nano\n", className, methodName, result, cost));
    }
}
