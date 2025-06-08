package com.flyfire.tool.service;

import com.alibaba.fastjson2.JSON;
import com.flyfire.tool.constant.DirVal;
import com.flyfire.tool.constant.SuvConst;
import com.flyfire.tool.util.SuvCmdUtil;
import com.flyfire.tool.util.dt.YTimes;
import com.flyfire.tool.util.io.YFileUtils;
import com.flyfire.tool.model.SuvPid;
import com.flyfire.tool.model.SuvProgram;
import com.flyfire.tool.model.SuvProgramCmd;
import com.flyfire.tool.util.str.StringAs;
import com.flyfire.tool.util.str.StringHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.Arrays;

@Service
public class SuvService {


    /**
     * @param suvProgram
     */
    public synchronized void startProgram(SuvProgram suvProgram) {
        if (suvProgram.getIsRun() == 1) {
            throw new RuntimeException("程序已经启动");
        }
        String program = suvProgram.getName();
        String winPath = StringAs.simpleReplaceAll(suvProgram.getDirection(), "/", "\\");
        String[] commandArr = suvProgram.getCommand().split(" ");
        SuvProgramCmd cmd = new SuvProgramCmd();
        cmd.execDir = winPath;
        cmd.commands = Arrays.stream(commandArr).filter(item -> StringHelper.isNotBlank(item)).toArray(String[]::new);
        cmd.environment = suvProgram.getEnvironment();
        if (!suvProgram.isRetryStart()) {
            suvProgram.getRetryCount().set(10);
        }
        String pidFile = DirVal.DATA_PID.appendFilePath(cmd.pid + "_" + suvProgram.getName() + ".json");
        suvProgram.setSuvProgramCmd(cmd);
        cmd.fnFinish = (exitRes) -> {
            try {
                suvProgram.ps.write(("\r\n\n" + program + "======end====\r\n").getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                e.printStackTrace();
            }
            YFileUtils.delete(pidFile);
            suvProgram.setIsRun(0);
            suvProgram.setState("Stop");
            if (exitRes.getException() == null) {
                System.out.println(program + "正常退出");
                return;
            }
            if (suvProgram.getRetryCount().get() > 0) {
                String str = "====\r\n\n开始重试,剩余重试次数" + suvProgram.getRetryCount() + "===\r\n";
                suvProgram.getRetryCount().addAndGet(-1);
                try {
                    suvProgram.ps.write((str).getBytes(StandardCharsets.UTF_8));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                suvProgram.setRetryStart(true);
                startProgram(suvProgram);
            }
        };
        String description = "pid %s, startTime %s";
        suvProgram.setDescription(String.format(description, cmd.pid, LocalTime.now().format(YTimes.HMS)));
        /*SuvCmdUtil.redirectOutput(process.getErrorStream(), cmd.errorPs, null);*/
        SuvCmdUtil.exec(suvProgram);
        //  更改状态为成功
        suvProgram.setIsRun(1);
        suvProgram.setState("Running");
        SuvPid suvPid = new SuvPid();
        suvPid.setPid(cmd.pid).setProgram(program);
        String json = JSON.toJSONString(suvPid);
        YFileUtils.overwrite(pidFile, json);
    }

    public synchronized void stopProgram(String program) {
        SuvProgram suvProgram = SuvConst.SUV_PROGRAM_MAP.get(program);
        if (suvProgram.getIsRun() == 0) {
            throw new RuntimeException("程序已经关闭");
        }
        SuvProgramCmd cmd = suvProgram.getSuvProgramCmd();
        //  杀掉进程
        /*cmd.process.destroy();*/
        //  强制结束进程
        /*cmd.process.destroyForcibly();*/
        System.out.println("释放进程进程pid:" + cmd.pid);
        //  重试次数为0
        suvProgram.getRetryCount().set(0);
        SuvCmdUtil.kill(cmd.pid);
        System.out.println("释放完毕");
    }

}
