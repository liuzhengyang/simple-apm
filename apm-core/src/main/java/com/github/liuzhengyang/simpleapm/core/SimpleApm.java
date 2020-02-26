package com.github.liuzhengyang.simpleapm.core;

import java.util.Arrays;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.tools.attach.VirtualMachine;

/**
 * @author liuzhengyang
 * Make something people want.
 * 2020/2/26
 */
public class SimpleApm {
    private static final Logger logger = LoggerFactory.getLogger(SimpleApm.class);

    public static void main(String[] args) {
        if (args == null || args.length < 2) {
            logger.error("args not valid {}", Arrays.toString(args));
            return;
        }
        CommandLineParser parser = new DefaultParser();
        CommandLine commandLine;
        try {
            Options options = new Options();
            options.addOption("pid", "pid", true, "pid");
            options.addOption("agentPath", "agentPath", true, "agentPath");
            commandLine = parser.parse(options, args);
        } catch (ParseException e) {
            logger.error("Invalid arguments {}", Arrays.toString(args), e);
            return;
        }
        logger.info("Args {}", Arrays.toString(args));
        String agentPath = commandLine.getOptionValue("agentPath");
        String targetPid = commandLine.getOptionValue("pid");
        try {
            logger.info("Attaching {} {}", targetPid, agentPath);
            VirtualMachine attach = VirtualMachine.attach(targetPid);
            attach.loadAgent(agentPath);
            attach.detach();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
