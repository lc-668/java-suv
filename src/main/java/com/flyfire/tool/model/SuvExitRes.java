package com.flyfire.tool.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class SuvExitRes {

    private Integer exitValue;

    private Exception exception;

}
