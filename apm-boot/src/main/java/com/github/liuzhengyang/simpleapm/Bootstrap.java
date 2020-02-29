package com.github.liuzhengyang.simpleapm;

import static com.github.liuzhengyang.simpleapm.common.ApmVersionUtil.getLatestVersion;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.liuzhengyang.simpleapm.common.JavaHomeUtil;
import com.github.liuzhengyang.simpleapm.common.JavaVersionUtil;

/**
 * @author liuzhengyang
 * Created on 2019-10-28
 */
public class Bootstrap {

    private static final Logger logger = LoggerFactory.getLogger(Bootstrap.class.getSimpleName());

    static class JvmProcess {
        private String pid;
        private String commandLine;

        public JvmProcess(String pid, String commandLine) {
            this.pid = pid;
            this.commandLine = commandLine;
        }

        public String getPid() {
            return pid;
        }

        public String getCommandLine() {
            return commandLine;
        }
    }

    public static void main(String[] args) throws IOException {
        logger.info("Hello there, welcome to simple apm, the best trouble-shooting tool in the world");
        logger.info("Found existing java process, please choose one and hit RETURN.");
        Map<Integer, JvmProcess> processByIndex = getLocalProcesses();
        processByIndex.forEach((index, process) -> {
            logger.info("[{}]: {} {}", index, process.getPid(), process.getCommandLine());
        });
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        int index = NumberUtils.toInt(input);
        JvmProcess process = processByIndex.get(index);
        if (process == null) {
            logger.error("Target process {} not found", input);
            return;
        }
        // try start agent core with tools.jar
        List<String> command = new ArrayList<>();
        command.add("java");
        if (JavaVersionUtil.isLessThanJava9()) {
            File toolsJar = JavaHomeUtil.getToolsJar();
            if (toolsJar == null || !toolsJar.exists()) {
                logger.error("Tools jar not found");
                return;
            }
            command.add("-Xbootclasspath/a:" + JavaHomeUtil.getToolsJar().getAbsolutePath());
        }
        command.add("-jar");
        File localApmCoreJarFiles = findLocalApmCoreJarFiles();
        command.add(localApmCoreJarFiles.getAbsolutePath());
        command.add("--pid=" + process.getPid());
        File agentJarFile = findLocalApmAgentJarFiles();
        if (agentJarFile == null){
            logger.error("Agent jar not found");
            return;
        }
        command.add("--agentPath=" + agentJarFile.getAbsolutePath());
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.start();
    }

    private static Map<Integer, JvmProcess> getLocalProcesses() throws IOException {
        Map<Integer, JvmProcess> jvmProcessMap = new HashMap<>();
        List<String> command = new ArrayList<>();
        command.add("jps");
        command.add("-lvm");
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        List<String> jpsResult = new ArrayList<>();
        Process process = processBuilder.start();
        InputStream inputStream = process.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        AtomicInteger counter = new AtomicInteger();
        while ((line = br.readLine()) != null) {
            jpsResult.add(line);
            String[] splits = line.split(" ");
            JvmProcess jvmProcess = new JvmProcess(splits[0], splits[1]);
            jvmProcessMap.put(counter.incrementAndGet(), jvmProcess);
        }
        logger.info("Jps Result {}", jpsResult);
        return jvmProcessMap;
    }

    private static File findLocalApmCoreJarFiles() {
        File targetFile = new File(System.getProperty("user.home") + "/.simpleapm/", "apm-core-" + getLatestVersion() + "-jar-with-dependencies.jar");
        targetFile.getParentFile().mkdirs();
        if (targetFile.exists()) {
            return targetFile;
        }
        logger.info("Apm Agent jar not found {}, downloading...", targetFile.getAbsolutePath());
        String url = "https://maven.aliyun.com/repository/public/com/github/liuzhengyang/apm-core/" + getLatestVersion() + "/apm-core-" + getLatestVersion() + "-jar-with-dependencies.jar";
        try {
            InputStream inputStream = openUrlStream(url);
            Files.copy(inputStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return targetFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static File findLocalApmAgentJarFiles() {
        File targetFile = new File(System.getProperty("user.home") + "/.simpleapm/", "apm-agent-" + getLatestVersion() + " -jar-with-dependencies.jar");
        targetFile.getParentFile().mkdirs();
        if (targetFile.exists()) {
            return targetFile;
        }
        logger.info("Apm Agent jar not found {}, downloading...", targetFile.getAbsolutePath());
        String url = "https://maven.aliyun.com/repository/public/com/github/liuzhengyang/apm-agent/" + getLatestVersion() + "/apm-agent-" + getLatestVersion() + "-jar-with-dependencies.jar";
        try {
            InputStream inputStream = openUrlStream(url);
            Files.copy(inputStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return targetFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static InputStream openUrlStream(String url) throws IOException {
        URL targetUrl = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) targetUrl.openConnection();
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_MOVED_PERM ||
                responseCode == HttpURLConnection.HTTP_MOVED_TEMP) {
            String location = conn.getHeaderField("Location");
            return openUrlStream(location);
        }
        return conn.getInputStream();
    }

    private static String getToolsJarPath() {
        return null;
    }

    /**
     * 1. Try to find java home from System Property java.home
     * 2. If jdk > 8, FOUND_JAVA_HOME set to java.home
     * 3. If jdk <=8, try to find tools.jar under java.home
     *
     * @return
     */
    private static String getJavaHome() {
        return null;
    }
}
