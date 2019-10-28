package com.github.liuzhengyang.simpleapm.agent;

import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

/**
 * commandType extParams
 * @author liuzhengyang
 * Created on 2019-10-28
 */
public class ApmCommandDecoder extends MessageToMessageDecoder<String> {
    private static final Logger logger = LoggerFactory.getLogger(ApmCommandDecoder.class);

    private final Splitter commandSplitter = Splitter.on(Pattern.compile("\\s+"));

    protected void decode(ChannelHandlerContext channelHandlerContext, String s, List<Object> list) throws Exception {
        List<String> splits = commandSplitter.splitToList(s);
        System.out.println(splits);
        if (splits.size() < 1) {
            channelHandlerContext.write("Illegal command " + s);
        }
        String commandStr = splits.get(0);
        Command command = Command.getByCommand(commandStr);
        ApmCommand apmCommand = new ApmCommand();
        apmCommand.setCommandType(command);
        if (splits.size() > 1) {
            apmCommand.setArgs(splits.subList(1, splits.size()));
        }
        list.add(apmCommand);
    }

    enum Command {
        HELP("help"),
        WATCH("watch"),
        MONITOR("monitor"),
        CLASS_LOADER("classloader"),
        SEARCH_CLASS("sc"),
        ;
        private final String name;
        static Map<String, Command> commandByName = new HashMap<String, Command>();

        Command(String name) {
            this.name = name;
        }


        public static Command getByCommand(String command) {
            return Arrays.stream(values())
                    .filter(e -> e.name.equalsIgnoreCase(command))
                    .findFirst()
                    .orElse(HELP);
        }
    }
}
