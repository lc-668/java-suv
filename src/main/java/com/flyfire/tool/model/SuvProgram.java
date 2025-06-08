package com.flyfire.tool.model;

import com.flyfire.tool.stream.SuvOutStream;
import com.flyfire.tool.util.TimeIntervalExec;
import com.flyfire.tool.util.dt.YTimes;
import lombok.Data;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Data
public class SuvProgram extends SuvProgramMeta {

    /**
     * 是否运行
     */
    private volatile int isRun = 0;

    private boolean autostart = false;

    private String label;

    private List<String> labelList=new ArrayList<>();

    /**
     * 是否是手动停止，则会将重试次数置为 0
     */
    private volatile AtomicInteger retryCount = new AtomicInteger(0);

    /**
     * 是否在重试启动中,如果是重试启动中,则不会设置重试次数为多次重试
     */
    private volatile boolean isRetryStart;

    /**
     * 是否如果挂掉就重启
     */
    private boolean restart = false;

    /**
     * 状态
     */
    private volatile String state = "stop";

    private volatile Long pid;

    private String logDir;

    private String exec = "apache";

    private volatile String description = "pid ?, startTime " + LocalTime.now().format(YTimes.HMS);

    /**
     * 是否默认开启环境优化。一旦开启。如果python 环境没有添加
     * PYTHONUNBUFFERED 则会自动添加
     */
    private Integer envOptimization = 1;

    public SuvProgramCmd suvProgramCmd;

    public SuvOutStream ps = new SuvOutStream();

    public TimeIntervalExec restartInterval = new TimeIntervalExec().setInterval(30);


}
