package com.flyfire.tool.model;

import lombok.Data;

import java.util.Map;

@Data
public class SuvProgramMeta {


    private String name;
    //  工作目录
    private String direction;
    //  启动命令
    private String command;

    private String label;

    private String exec="apache";

    private Map<String, String> environment;

    /**
     * 是否自动重启
     */
    private boolean autostart = false;

}
