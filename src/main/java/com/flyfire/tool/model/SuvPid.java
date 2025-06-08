package com.flyfire.tool.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SuvPid {

    private Long pid;

    private String program;

}
