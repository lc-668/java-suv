package com.txkj.tool.constant;


import com.txkj.tool.util.io.YDynamicDir;
import com.txkj.tool.util.str.StringHelper;

public class DirVal {

    public static YDynamicDir DATA_HOME;
    public static YDynamicDir DATA_PID;

    public static void init(String dataHomeStr) {
        if (StringHelper.isBlank(dataHomeStr)) {
            System.out.println("获取当前程序的运行路径");
            dataHomeStr = System.getenv("suv_data");
            /*dataHomeStr = YPathUtils.joinStrPath(dataHomeStr, "suv_data");*/
            if(dataHomeStr==null){
                System.out.println("请配置 环境变量 【suv_data】用于存放数据目录");
                System.exit(1);
            }
        }
        System.out.println("当前的程序路径:" + dataHomeStr);
        DATA_HOME = new YDynamicDir(dataHomeStr);
        DATA_PID = DATA_HOME.getChildDir("pid");
    }

}
