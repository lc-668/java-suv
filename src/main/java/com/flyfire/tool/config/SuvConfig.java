package com.flyfire.tool.config;

import com.alibaba.fastjson2.JSON;
import com.flyfire.tool.constant.DirVal;
import com.flyfire.tool.constant.SuvConst;
import com.flyfire.tool.service.ResetService;
import com.flyfire.tool.util.YmlUtil;
import com.flyfire.tool.util.io.YFileDirUtil;
import com.flyfire.tool.util.io.YFileUtils;
import com.flyfire.tool.util.io.YLogWriter;
import com.flyfire.tool.model.SuvProgram;
import com.flyfire.tool.model.SuvProgramMeta;
import com.flyfire.tool.util.str.StringAs;
import com.flyfire.tool.util.str.StringHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class SuvConfig {

    @Autowired
    public void init(Environment environment, SuvSpringConfigAttr attr, ResetService resetService) {
        System.out.println(attr.getPath());
        //  优先从环境变量加载
        String str = environment.getProperty("suv.config.path");
        String str_env=System.getenv("suv_config");
        if(StringHelper.isNotBlank(str_env)){
            str=str_env;
        }
        DirVal.init(null);
        resetService.killOldPid();
        String content = readCount(str);
        System.out.println("suv内容\r\n" + content);
        var json = YmlUtil.ymlToJson(content);
        var jsonOBJ = JSON.parseObject(json);
        var programMap = jsonOBJ.getJSONObject("suv").getJSONObject("program");
        String parentLogDir = jsonOBJ.getJSONObject("suv").getString("logDir");
        programMap.forEach((k, v) -> {
            String jsonStr = JSON.toJSONString(v);
            SuvProgramMeta meta = JSON.parseObject(jsonStr, SuvProgramMeta.class);
            SuvProgram suvProgram = new SuvProgram();
            BeanUtils.copyProperties(meta, suvProgram);
            suvProgram.setName(k);
            if (StringHelper.isNotBlank(parentLogDir)) {
                String logDir = parentLogDir + "/" + k;
                YFileDirUtil.recCreateDir(logDir);
                suvProgram.ps.logWriter = new YLogWriter(logDir, 1024, 10);
            }
            //  #{suv.project.dir}
            var cmd = suvProgram.getCommand();
            cmd = StringAs.simpleReplaceAll(cmd, "#{suv.project.dir}", meta.getDirection());
            cmd = StringAs.simpleReplaceAll(cmd, "#{spd}", meta.getDirection());
            //  配置环境
            suvProgram.setCommand(cmd);
            //  配置 item
            parseLabel(suvProgram);
            parseEnv(suvProgram);
            SuvConst.SUV_PROGRAMS.add(suvProgram);
            SuvConst.SUV_PROGRAM_MAP.put(suvProgram.getName(), suvProgram);
        });
        System.out.println("suv.program 读取配置完毕");
        System.out.println(SuvConst.SUV_PROGRAMS);
        System.out.println("================= 进入开机启动 流程 =================");
        resetService.autoStartupProgram();
    }

    public String readCount(String str) {
        if (StringHelper.isBlank(str)) {
            System.out.println("由于你的suv路径为null,默认从jar包加载");
            return YFileUtils.readJarResContent("suv.yml");
        }
        var file = new File(str);
        if (!file.exists()) {
            System.out.println("由于你的suv路径【不存在】,默认从jar包加载");
            return YFileUtils.readJarResContent("suv.yml");
        }
        return YFileUtils.readContent(file, Charset.defaultCharset());
    }

    public void parseEnv(SuvProgram suvProgram) {
        if (Objects.equals(suvProgram.getEnvOptimization(), 1)) {
            if (suvProgram.getCommand().contains("python")) {
                if (suvProgram.getEnvironment() == null) {
                    suvProgram.setEnvironment(new ConcurrentHashMap<>());
                }
                suvProgram.getEnvironment().putIfAbsent("PYTHONUNBUFFERED", "1");
                suvProgram.getEnvironment().putIfAbsent("PYTHONIOENCODING", "utf-8");
            }
        }
    }

    public void parseLabel(SuvProgram suvProgram) {
        String label = suvProgram.getLabel();
        if (StringUtils.isEmpty(label)) {
            suvProgram.setLabel("");
            return;
        }
        String[] split = label.split(",");
        for (String item : split) {
            suvProgram.getLabelList().add(item);
        }
    }

}
