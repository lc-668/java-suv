package com.flyfire.tool.service;

import com.alibaba.fastjson2.JSON;
import com.flyfire.tool.constant.DirVal;
import com.flyfire.tool.constant.SuvConst;
import com.flyfire.tool.util.SuvCmdUtil;
import com.flyfire.tool.util.io.YFileUtils;
import com.flyfire.tool.util.io.YPathUtils;
import com.flyfire.tool.model.SuvPid;
import com.flyfire.tool.model.SuvProgram;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

/**
 * 启动后的业务：
 * 包括删除过期的pid
 */
@Service
public class ResetService {

    @Autowired
    private SuvService suvService;


    public void killOldPid() {
        String pidPath = DirVal.DATA_PID.getDynamicDir();
        List<String> list = YPathUtils.list(pidPath);
        for (String item : list) {
            String jsonStr = YFileUtils.readContent(new File(item), null);
            SuvPid suvPid = JSON.parseObject(jsonStr, SuvPid.class);
            System.out.println("检测到旧有的活动进程:" + suvPid.getProgram() + " 开始杀掉相关进程" + suvPid.getPid());
            SuvCmdUtil.kill(suvPid.getPid());
            YFileUtils.delete(item);
        }
    }

    /**
     * 自动启动某些应该启动的程序
     */
    public void autoStartupProgram() {
        for (SuvProgram suvProgram : SuvConst.SUV_PROGRAMS) {
            if (suvProgram.isAutostart()) {
                System.out.println("正在开机启动某些程序:" + suvProgram.getName());
                suvProgram.setRetryStart(false);
                suvService.startProgram(suvProgram);
            }
        }
    }

}
