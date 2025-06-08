package com.flyfire.tool.model;

import com.flyfire.tool.opw.MyApaceCommExecutor;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class SuvProgramCmd {

    public long pid;

    public String execDir;
    public String[] commands;

    public MyApaceCommExecutor executor;

    public Map<String, String> environment;

    /**
     * 子进程id
     */
    public List<Long> childPidList;

    /**
     *  会传入 exitValue
     */
    public Consumer<SuvExitRes> fnFinish;

}
