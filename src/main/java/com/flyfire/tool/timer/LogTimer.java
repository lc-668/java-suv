package com.flyfire.tool.timer;

import com.flyfire.tool.constant.SuvConst;
import com.flyfire.tool.model.SuvProgram;
import com.flyfire.tool.util.io.YLogWriter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class LogTimer {

    @Scheduled(cron = "1 */10 * * * *")
    public void flushWrite() {
        System.out.println("定时清空日志的消息");
        for (SuvProgram suvProgram : SuvConst.SUV_PROGRAMS) {
            YLogWriter logWriter = suvProgram.ps.logWriter;
            if (logWriter == null) {
                return;
            }
            logWriter.flush();

        }
    }

}
