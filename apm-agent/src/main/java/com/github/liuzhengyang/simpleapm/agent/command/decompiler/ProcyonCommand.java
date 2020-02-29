package com.github.liuzhengyang.simpleapm.agent.command.decompiler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.liuzhengyang.simpleapm.agent.util.CommandProcessUtil;
import com.github.liuzhengyang.simpleapm.agent.util.TypeUtil;
import com.strobel.decompiler.Decompiler;
import com.strobel.decompiler.PlainTextOutput;

import io.vertx.core.cli.annotations.Argument;
import io.vertx.core.cli.annotations.Name;
import io.vertx.core.cli.annotations.Summary;
import io.vertx.ext.shell.command.AnnotatedCommand;
import io.vertx.ext.shell.command.CommandProcess;

/**
 * @author liuzhengyang
 * Make something people want.
 * 2020/2/29
 */
@Name("decompile")
@Summary("decompile class to java source")
public class ProcyonCommand extends AnnotatedCommand {
    private static final Logger logger = LoggerFactory.getLogger(ProcyonCommand.class);

    private String className;

    public String getClassName() {
        return className;
    }

    @Argument(argName = "className", index = 0)
    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public void process(CommandProcess commandProcess) {
        String internalName = TypeUtil.getInternalName(getClassName());
        PlainTextOutput plainTextOutput = new PlainTextOutput();
        Decompiler.decompile(internalName, plainTextOutput);
        logger.info("Decompile {} result {}", getClassName(), plainTextOutput.toString());
        CommandProcessUtil.println(commandProcess, "%s", plainTextOutput.toString());
        commandProcess.end();
    }
}
