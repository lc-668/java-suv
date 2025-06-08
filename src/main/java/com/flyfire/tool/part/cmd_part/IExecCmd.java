package com.flyfire.tool.part.cmd_part;

import com.flyfire.tool.model.SuvProgramCmd;
import com.flyfire.tool.stream.SuvOutStream;

public interface IExecCmd {

    void exec(SuvProgramCmd suvProgramCmd, SuvOutStream ps);

}
