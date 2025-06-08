package com.txkj.tool.opw;

import lombok.SneakyThrows;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteStreamHandler;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * 因为他的旧有方法没有提供所谓的pid的获取。所以需要我这边主动获取
 */
public class MyApaceCommExecutor extends DefaultExecutor {

    protected Process process;

    public long getPid() {
        if (process == null) {
            return 0;
        }
        return process.pid();
    }

    @SneakyThrows
    public long waitForPid() {
        if (process == null) {
            Thread.sleep(1000);
            return waitForPid();
        }
        return process.pid();
    }

    @Override
    protected Process launch(CommandLine command, Map<String, String> env, File workingDirectory) throws IOException {
        process = super.launch(command, env, workingDirectory);
        return process;
    }
}
