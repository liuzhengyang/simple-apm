package com.github.liuzhengyang.simpleapm.agent.command.decompiler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.liuzhengyang.simpleapm.agent.util.CommandProcessUtil;
import com.github.liuzhengyang.simpleapm.agent.util.TypeUtil;
import com.strobel.decompiler.Decompiler;
import com.strobel.decompiler.DecompilerSettings;
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
        String decompiledClass = getDecompiledClass(getClassName());
        CommandProcessUtil.println(commandProcess, "%s", decompiledClass);
        commandProcess.end();
    }

    public static String getDecompiledClass(String className) {
        String internalName = TypeUtil.getInternalName(className);
        PlainTextOutput plainTextOutput = new PlainTextOutput();
        DecompilerSettings decompilerSettings = new DecompilerSettings();
        decompilerSettings.setIncludeLineNumbersInBytecode(false);
        decompilerSettings.setShowDebugLineNumbers(false);
        Decompiler.decompile(internalName, plainTextOutput, decompilerSettings);
        logger.info("Decompile {} result {}", internalName, plainTextOutput.toString());
        return plainTextOutput.toString();
    }
}
