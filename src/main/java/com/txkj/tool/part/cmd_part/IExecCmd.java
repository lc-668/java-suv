package com.txkj.tool.part.cmd_part;

import com.txkj.tool.model.SuvProgramCmd;
import com.txkj.tool.stream.SuvOutStream;

public interface IExecCmd {

    void exec(SuvProgramCmd suvProgramCmd, SuvOutStream ps);

}
