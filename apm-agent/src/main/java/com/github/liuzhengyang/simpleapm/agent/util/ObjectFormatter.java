package com.github.liuzhengyang.simpleapm.agent.util;

import java.util.HashMap;
import java.util.Map;

import org.mvel2.MVEL;

public class ObjectFormatter {
    public static String pattern;

    public static void setPattern(String pattern) {
        ObjectFormatter.pattern = pattern;
    }

    public static String format(Object o) {
        if (pattern == null) {
            return JsonUtils.toJson(o);
        }
        Map<String, Object> vars = new HashMap<>();
        vars.put("result", o);
        return JsonUtils.toJson(MVEL.eval(pattern, vars));
    }
}
