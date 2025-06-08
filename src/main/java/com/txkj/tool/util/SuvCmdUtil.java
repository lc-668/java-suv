package com.txkj.tool.util;

import com.txkj.tool.model.SuvProgram;
import com.txkj.tool.model.SuvProgramCmd;
import com.txkj.tool.part.cmd_part.ApacheExecCmd;
import com.txkj.tool.part.cmd_part.IExecCmd;
import com.txkj.tool.part.cmd_part.OriExecCmd;
import com.txkj.tool.stream.SuvOutStream;
import com.txkj.tool.util.str.StringAs;
import lombok.SneakyThrows;

import java.io.IOException;

public class SuvCmdUtil {

    @SneakyThrows
    public static void exec(SuvProgram suvProgram) {
        SuvProgramCmd cmd = suvProgram.suvProgramCmd;
        SuvOutStream ps = suvProgram.ps;
        IExecCmd execCmd = null;
        if ("apache".equals(suvProgram.getExec())) {
            execCmd = new ApacheExecCmd();
        } else if ("ori".equals(suvProgram.getExec())) {
            execCmd = new OriExecCmd();
        } else {
            throw new RuntimeException("指定的执行器不存在:" + suvProgram.getExec());
        }
        execCmd.exec(cmd, ps);
    }

    public static void kill(long pid) {
        Runtime rt = Runtime.getRuntime();
        try {
            if (System.getProperty("os.name").toLowerCase().indexOf("windows") > -1) {
                /*
                1,根据pid终止进程: TASKKILL /F /pid 123456
                2,根据进程的名称终止进程: TASKKILL /F /IM xxx.exe
                /F   指定强制终止进程。
                 */
                /*rt.exec("taskkill /F /pid " +pid);*/
                //  必须强制关闭才行。。。 特别是抖音这种流氓软件
                //  taskkill /F /T /PID [PID号]
                //  /T代表同时杀掉子进程
                String cmdStr1 = "taskkill /T /F /pid " + pid;
                System.out.println(cmdStr1);
                rt.exec(cmdStr1);
            } else
                rt.exec("killLike -9" + pid);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static Integer getPid(int pid) {
        String cmdStr = "wmic process where ProcessId=" + pid + " get ParentProcessId";
        String result = Cmder.executeCmdCommand(cmdStr);
        result = StringAs.simpleReplaceAll(result, "ParentProcessId", "");
        result = StringAs.simpleReplaceAll(result, "\n", "");
        result = StringAs.simpleReplaceAll(result, " ", "");
        System.out.println(result);
        Integer parentId = null;
        return 0;
    }

    public static void main(String[] args) {
        getPid(14588);
    }


}
