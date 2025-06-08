package com.txkj.tool.part.cmd_part;

import com.txkj.tool.model.SuvExitRes;
import com.txkj.tool.model.SuvProgramCmd;
import com.txkj.tool.opw.MyApaceCommExecutor;
import com.txkj.tool.stream.SuvOutStream;
import lombok.SneakyThrows;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.PumpStreamHandler;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

public class ApacheExecCmd implements IExecCmd {

    @SneakyThrows
    @Override
    public void exec(SuvProgramCmd suvProgramCmd, SuvOutStream ps) {
        var commands = suvProgramCmd.commands;
        CommandLine commandLine = new CommandLine(commands[0]);
        for (int i = 1; i < commands.length; i++) {
            commandLine.addArgument(commands[i]);
        }
        // 创建 DefaultExecutor 实例
        MyApaceCommExecutor executor = new MyApaceCommExecutor();
        suvProgramCmd.executor = executor;
        // 设置超时时间(如果设置了就会导致超时自动关闭这个子进程了。)
        /*ExecuteWatchdog watchdog = new ExecuteWatchdog(60000); // 1分钟超时()
        executor.setWatchdog(watchdog);*/

        // 设置环境变量
        /*Map<String, String> environment = new HashMap<>();
        environment.put("PYTHONUNBUFFERED", "1");*/

        // 设置工作目录
        File workingDir = new File(suvProgramCmd.execDir);
        executor.setWorkingDirectory(workingDir);

        // 设置输入输出流
        InputStream in = System.in;
        OutputStream out = ps;
        OutputStream err = ps;
        PumpStreamHandler streamHandler = new PumpStreamHandler(out, err, in);
        executor.setStreamHandler(streamHandler);
        // 创建 DefaultExecuteResultHandler 实例
        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler() {
            @Override
            public void onProcessComplete(int exitValue) {
                super.onProcessComplete(exitValue);
                System.out.println("命令执行成功");
                if (suvProgramCmd.fnFinish != null)
                    suvProgramCmd.fnFinish.accept(new SuvExitRes().setExitValue(exitValue));
            }

            @Override
            public void onProcessFailed(ExecuteException e) {
                super.onProcessFailed(e);
                System.out.println("命令执行失败");
                e.printStackTrace();
                if (suvProgramCmd.fnFinish != null)
                    suvProgramCmd.fnFinish.accept(new SuvExitRes().setException(e));
            }

        };
        // Execute the command and get the process
        // 异步执行命令
        executor.execute(commandLine, suvProgramCmd.environment, resultHandler);
        // 获取子进程 PID
        long pid = executor.waitForPid();
        if (pid > 0) {
            System.out.println("子进程的 PID 是：" + pid);
            suvProgramCmd.pid = pid;
        } else {
            System.out.println("无法获取子进程的 PID。");
        }
    }
}
