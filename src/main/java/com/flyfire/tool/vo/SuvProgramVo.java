package com.flyfire.tool.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SuvProgramVo {

    private String name;

    /**
     * 是否运行
     */
    private int isRun = 0;

    /**
     * 状态
     */
    private String state = "stop";

    private String description = "pid 11012, uptime 0:57:08";

    private String label;

    private List<String> labelList=new ArrayList<>();

}
